/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import org.myire.scent.metrics.MethodMetrics;


/**
 * Unit tests related to parsing and collecting metrics for native methods.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class NativeMethodCollectTest extends MethodCollectTestBase
{
    @Override
    protected MethodMetrics.Kind getMethodKind()
    {
        return MethodMetrics.Kind.NATIVE_METHOD;
    }


    @Override
    protected String createMethodName()
    {
        return "void someNativeMethod()";
    }


    @Override
    protected String createMethodDeclaration(String pName)
    {
        return "native " + pName + ';';
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "class AnyClass {";
    }
}
