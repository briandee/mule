/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.providers.dq;

import org.mule.tck.NamedTestCase;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.impl.endpoint.MuleEndpointURI;

/**
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class DQEndpointTestCase extends NamedTestCase
{
    public void testWithoutLibParam() throws Exception
    {
        UMOEndpointURI url = new MuleEndpointURI("dq://QSYS.LIB/L701QUEUE.DTAQ");
        assertEquals("dq", url.getScheme());
        assertEquals("/QSYS.LIB/L701QUEUE.DTAQ", url.getAddress());
        assertNull(url.getEndpointName());
        assertEquals(1, url.getParams().size());
        assertEquals("QSYS.LIB", url.getParams().getProperty("lib"));
    }

    public void testWithLibParam() throws Exception
    {
        UMOEndpointURI url = new MuleEndpointURI("dq://L701QUEUE.DTAQ?lib=QSYS.LIB");
        assertEquals("dq", url.getScheme());
        assertEquals("/QSYS.LIB/L701QUEUE.DTAQ", url.getAddress());
        assertNull(url.getEndpointName());
        assertEquals(1, url.getParams().size());
        assertEquals("QSYS.LIB", url.getParams().getProperty("lib"));
    }
}
