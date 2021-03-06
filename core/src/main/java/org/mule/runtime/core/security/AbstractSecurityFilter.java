/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.security;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.security.CryptoFailureException;
import org.mule.runtime.core.api.security.EncryptionStrategyNotFoundException;
import org.mule.runtime.core.api.security.SecurityException;
import org.mule.runtime.core.api.security.SecurityFilter;
import org.mule.runtime.core.api.security.SecurityManager;
import org.mule.runtime.core.api.security.SecurityProvider;
import org.mule.runtime.core.api.security.SecurityProviderNotFoundException;
import org.mule.runtime.core.api.security.UnknownAuthenticationTypeException;
import org.mule.runtime.core.config.i18n.CoreMessages;
import org.mule.runtime.core.transformer.TransformerTemplate;
import org.mule.runtime.core.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>AbstractSecurityFilter</code> provides basic initialisation for
 * all security filters, namely configuring the SecurityManager for this instance
 */
public abstract class AbstractSecurityFilter implements MuleContextAware, SecurityFilter
{

    protected transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SecurityManager securityManager;
    protected MuleContext muleContext;

    private String securityProviders;

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    public final void initialise() throws InitialisationException
    {
        if (securityManager == null)
        {
            securityManager = muleContext.getSecurityManager();
        }
        
        if (securityManager == null)
        {
            throw new InitialisationException(CoreMessages.authSecurityManagerNotSet(), this);
        }

        // This filter may only allow authentication on a subset of registered
        // security providers
        if (securityProviders != null)
        {
            SecurityManager localManager = new MuleSecurityManager();
            String[] securityProviders = StringUtils.splitAndTrim(this.securityProviders, ",");
            for (String sp : securityProviders)
            {
                SecurityProvider provider = securityManager.getProvider(sp);
                if (provider != null)
                {
                    localManager.addProvider(provider);
                }
                else
                {
                    throw new InitialisationException(
                            CoreMessages.objectNotRegistered(
                                    "Security Provider", sp), this);
                }
            }
            securityManager = localManager;
        }
        
        doInitialise();
    }

    protected void doInitialise() throws InitialisationException
    {
    }

    /** @param manager  */
    public void setSecurityManager(SecurityManager manager)
    {
        securityManager = manager;
    }

    public SecurityManager getSecurityManager()
    {
        return securityManager;
    }

    public String getSecurityProviders()
    {
        return securityProviders;
    }

    public void setSecurityProviders(String providers)
    {
        securityProviders = providers;
    }

    public abstract void doFilter(MuleEvent event)
            throws SecurityException, UnknownAuthenticationTypeException, CryptoFailureException,
            SecurityProviderNotFoundException, EncryptionStrategyNotFoundException,
            InitialisationException;
    
    protected void updatePayload(MuleMessage message, final Object payload, MuleEvent event) throws MuleException
    {
        TransformerTemplate trans = new TransformerTemplate(new TransformerTemplate.TransformerCallback()
        {
            public Object doTransform(MuleMessage message) throws Exception
            {
                return payload;
            }
        });

        event.setMessage(muleContext.getTransformationService().applyTransformers(event.getMessage(), event, trans));
    }
}
