/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import org.myire.scent.metrics.MethodMetrics;


/**
 * Unit tests related to parsing and collecting metrics for instance methods.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class InstanceMethodCollectTest extends MethodWithBodyCollectTestBase
{
    @Override
    protected MethodMetrics.Kind getMethodKind()
    {
        return MethodMetrics.Kind.INSTANCE_METHOD;
    }


    @Override
    protected String createMethodName()
    {
        return "void someInstanceMethod()";
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "class AnyClass {";
    }
}
