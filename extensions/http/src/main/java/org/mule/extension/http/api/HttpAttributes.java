/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api;

import static com.google.common.collect.ImmutableMap.copyOf;

import org.mule.runtime.module.http.internal.ParameterMap;

import java.io.Serializable;
import java.util.Map;

import javax.activation.DataHandler;

/**
 * Base representation of HTTP message attributes.
 *
 * @since 4.0
 */
public abstract class HttpAttributes implements Serializable
{
    /**
     * Map of HTTP headers in the message. Former properties.
     */
    protected final ParameterMap headers;
    /**
     * Map of HTTP parts in the message (from multipart content). Former attachments.
     */
    protected final Map<String, DataHandler> parts;

    public HttpAttributes(ParameterMap headers, Map<String, DataHandler> parts)
    {
        this.headers = headers.toImmutableParameterMap();
        this.parts = copyOf(parts);
    }

    public ParameterMap getHeaders()
    {
        return headers;
    }
    public Map<String, DataHandler> getParts()
    {
        return parts;
    }
}
