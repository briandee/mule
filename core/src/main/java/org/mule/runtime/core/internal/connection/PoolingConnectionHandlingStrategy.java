/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.connection;

import static org.mule.runtime.core.config.i18n.MessageFactory.createStaticMessage;
import org.mule.runtime.api.config.PoolingProfile;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandler;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingListener;
import org.mule.runtime.core.api.DefaultMuleException;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleException;

import java.util.NoSuchElementException;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConnectionHandlingStrategyAdapter} which returns connections obtained from a {@link #pool}
 *
 * @param <Connection> the generic type of the connections to be managed
 * @since 4.0
 */
final class PoolingConnectionHandlingStrategy<Connection> extends ConnectionHandlingStrategyAdapter<Connection>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolingConnectionHandlingStrategy.class);
    private static final String NULL_VALIDATION_RESULT_ERROR_MESSAGE = "Error validating connection. ConnectionValidationResult can not be null";

    private final PoolingProfile poolingProfile;
    private final ObjectPool<Connection> pool;
    private final PoolingListener<Connection> poolingListener;

    /**
     * Creates a new instance
     *
     * @param connectionProvider the {@link ConnectionProvider} used to manage the connections
     * @param poolingProfile     the {@link PoolingProfile} which configures the {@link #pool}
     * @param poolingListener    a {@link PoolingListener}
     * @param muleContext        the application's {@link MuleContext}
     */
    PoolingConnectionHandlingStrategy(ConnectionProvider<Connection> connectionProvider,
                                      PoolingProfile poolingProfile,
                                      PoolingListener<Connection> poolingListener,
                                      MuleContext muleContext)
    {
        super(connectionProvider, muleContext);
        this.poolingProfile = poolingProfile;
        this.poolingListener = poolingListener;
        pool = createPool();
    }

    /**
     * Returns a {@link ConnectionHandler} which wraps a connection obtained from the
     * {@link #pool}
     *
     * @return a {@link ConnectionHandler}
     * @throws ConnectionException if the connection could not be obtained
     */
    @Override
    public ConnectionHandler<Connection> getConnectionHandler() throws ConnectionException
    {
        try
        {
            Connection connection = borrowConnection();
            ConnectionValidationResult validationResult = connectionProvider.validate(connection);

            if (validationResult == null)
            {
                LOGGER.debug(NULL_VALIDATION_RESULT_ERROR_MESSAGE);
                pool.invalidateObject(connection);
                throw new ConnectionException(NULL_VALIDATION_RESULT_ERROR_MESSAGE);
            }
            else if (!validationResult.isValid())
            {
                pool.invalidateObject(connection);
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Error validating connection: {}. Invalidating connection.", validationResult.getMessage());
                }
                throw new ConnectionException(validationResult.getMessage(), validationResult.getException());
            }

            return new PooledConnectionHandler<>(connection, pool, poolingListener);
        }
        catch (ConnectionException e)
        {
            throw e;
        }
        catch (NoSuchElementException e)
        {
            throw new ConnectionException("Connection pool is exhausted");
        }
        catch (Exception e)
        {
            throw new ConnectionException("An exception was found trying to obtain a connection", e);
        }
    }

    private Connection borrowConnection() throws Exception
    {
        Connection connection = pool.borrowObject();
        try
        {
            poolingListener.onBorrow(connection);
        }
        catch (Exception e)
        {
            pool.invalidateObject(connection);
            throw e;
        }

        return connection;
    }

    /**
     * Closes the pool, causing the contained connections to be closed as well.
     *
     * @throws MuleException
     */
    //TODO: MULE-9082 - pool.close() doesn't destroy unreturned connections
    @Override
    public void close() throws MuleException
    {
        try
        {
            pool.close();
        }
        catch (Exception e)
        {
            throw new DefaultMuleException(createStaticMessage("Could not close connection pool"), e);
        }
    }

    private ObjectPool<Connection> createPool()
    {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxIdle = poolingProfile.getMaxIdle();
        config.maxActive = poolingProfile.getMaxActive();
        config.maxWait = poolingProfile.getMaxWait();
        config.whenExhaustedAction = (byte) poolingProfile.getExhaustedAction();
        config.minEvictableIdleTimeMillis = poolingProfile.getMinEvictionMillis();
        config.timeBetweenEvictionRunsMillis = poolingProfile.getEvictionCheckIntervalMillis();
        GenericObjectPool genericPool = new GenericObjectPool(new ObjectFactoryAdapter(), config);

        return genericPool;
    }

    private class ObjectFactoryAdapter implements PoolableObjectFactory<Connection>
    {

        @Override
        public Connection makeObject() throws Exception
        {
            return connectionProvider.connect();
        }

        @Override
        public void destroyObject(Connection connection) throws Exception
        {
            connectionProvider.disconnect(connection);
        }

        @Override
        public boolean validateObject(Connection obj)
        {
            return false;
        }

        @Override
        public void activateObject(Connection connection) throws Exception
        {
        }

        @Override
        public void passivateObject(Connection connection) throws Exception
        {
        }
    }

    public PoolingProfile getPoolingProfile()
    {
        return poolingProfile;
    }
}
