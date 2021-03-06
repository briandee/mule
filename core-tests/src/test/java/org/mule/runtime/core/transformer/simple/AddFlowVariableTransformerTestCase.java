/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.transformer.simple;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.tck.size.SmallTest;

import static org.hamcrest.collection.IsEmptyCollection.empty;

@SmallTest
public class AddFlowVariableTransformerTestCase extends AbstractAddVariablePropertyTransformerTestCase
{

    public AddFlowVariableTransformerTestCase()
    {
        super(new AddFlowVariableTransformer());
    }

    @Override
    protected void verifyAdded(MuleEvent event, String key, String value)
    {
        assertThat(event.getFlowVariable(key), is(value));
    }

    @Override
    protected void verifyNotAdded(MuleEvent event)
    {
        assertThat(event.getFlowVariableNames(), empty());
    }

    @Override
    protected void verifyRemoved(MuleEvent event, String key)
    {
        assertThat(event.getFlowVariable(key), is(nullValue()));
    }

    @Override
    protected DataType getVariableDataType(MuleEvent event, String key)
    {
        return event.getFlowVariableDataType(key);
    }

}
