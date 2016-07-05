/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for annotations related stuff.
 *
 * @since 4.0
 */
public class AnnotationUtils
{

    private AnnotationUtils()
    {
    }

    /**
     * @param klass
     * @param annotationClass
     * @param methodName
     * @param <T>
     * @return the attribute from the annotation for the given class.
     */
    public static <T> T getAnnotationAttributeFrom(Class<?> klass, Class<? extends Annotation> annotationClass, String methodName)
    {
        T extensions;
        Annotation annotation = klass.getAnnotation(annotationClass);
        Method method;
        try
        {
            method = annotationClass.getMethod(methodName);

            if (annotation != null)
            {
                extensions = (T) method.invoke(annotation);
            }
            else
            {
                extensions = (T) method.getDefaultValue();

            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Cannot read default " + methodName + " from " + annotationClass);
        }

        return extensions;
    }

    /**
     * @param klass
     * @param annotationClass
     * @param methodName
     * @param <T>
     * @return a (@link List} of T for the attributes annotated in all the object hierarchy until it reaches {@link Object} class.
     */
    public static <T> List<T> getAnnotationAttributeFromHierarchy(Class<?> klass, Class<? extends Annotation> annotationClass, String methodName)
    {
        List<T> extensions = new ArrayList<>();
        Class<?> currentClass = klass;
        while (currentClass != Object.class)
        {
            extensions.add(getAnnotationAttributeFrom(klass, annotationClass, methodName));
            currentClass = currentClass.getSuperclass();
        }
        return extensions;
    }
}
