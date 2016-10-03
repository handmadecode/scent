/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import org.myire.scent.metrics.MethodMetrics;


/**
 * Unit tests related to parsing and collecting metrics for default methods in interfaces.
 *
 *  @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class DefaultMethodCollectTest extends MethodWithBodyCollectTestBase
{
    @Override
    protected MethodMetrics.Kind getMethodKind()
    {
        return MethodMetrics.Kind.DEFAULT_METHOD;
    }


    @Override
    protected String createMethodName()
    {
        return "void someDefaultMethod()";
    }


    @Override
    protected String createMethodDeclaration(String pName)
    {
        return "default " + pName + "{}";
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "interface AnyInterface {";
    }
}
