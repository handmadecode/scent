/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import org.myire.scent.metrics.MethodMetrics;


/**
 * Unit tests related to parsing and collecting metrics for constructors.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ConstructorCollectTest extends MethodWithBodyCollectTestBase
{
    static private final String TYPE_NAME = "TheType";


    @Override
    protected MethodMetrics.Kind getMethodKind()
    {
        return MethodMetrics.Kind.CONSTRUCTOR;
    }


    @Override
    protected String createMethodName()
    {
        return TYPE_NAME + "()";
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "class " + TYPE_NAME + " {";
    }
}
