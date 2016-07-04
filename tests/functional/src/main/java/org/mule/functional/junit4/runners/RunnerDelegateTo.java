/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Specifies the {@link Runner} that {@link ArtifactClassloaderTestRunner} delegates to.
 *
 * @since 4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface RunnerDelegateTo
{

    /**
     * @return the {@link Runner} that would be used to delegate the execution of the test.
     */
    Class<? extends Runner> value() default BlockJUnit4ClassRunner.class;

}