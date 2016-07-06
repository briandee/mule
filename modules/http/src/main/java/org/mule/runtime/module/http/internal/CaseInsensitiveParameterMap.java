/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.http.internal;

import static java.util.stream.Collectors.toCollection;
import org.mule.runtime.core.util.CaseInsensitiveMapWrapper;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CaseInsensitiveParameterMap extends ParameterMap
{

    public CaseInsensitiveParameterMap(ParameterMap paramsMap)
    {
        this.paramsMap = new CaseInsensitiveMapWrapper<>(LinkedHashMap.class);
        for (String key : paramsMap.keySet())
        {
            LinkedList<String> values = paramsMap.getAll(key).stream().collect(toCollection(LinkedList::new));
            this.paramsMap.put(key, values);
        }
    }

}
