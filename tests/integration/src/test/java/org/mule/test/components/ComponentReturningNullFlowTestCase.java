/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.components;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.message.NullPayload;

import org.junit.Test;

public class ComponentReturningNullFlowTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/components/component-returned-null-flow.xml";
    }

    @Test
    public void testNullReturnStopsFlow() throws Exception
    {
        MuleMessage msg = flowRunner("StopFlowService").withPayload(TEST_PAYLOAD).run().getMessage();
        assertNotNull(msg);
        final String payload = getPayloadAsString(msg);
        assertNotNull(payload);
        assertFalse("ERROR".equals(payload));
        assertTrue(msg.getPayload() instanceof NullPayload);
    }

    public static final class ComponentReturningNull
    {
        public String process(String input)
        {
            return null;
        }
    }
}
