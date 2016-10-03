/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.field;

import org.myire.scent.metrics.FieldMetrics;


/**
 * Unit tests related to parsing and collecting metrics for enum constants as fields. An enum
 * constant that has no body is effectively a field.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class EnumConstantCollectTest extends FieldCollectTestBase
{
    @Override
    protected FieldMetrics.Kind getFieldKind()
    {
        return FieldMetrics.Kind.ENUM_CONSTANT;
    }


    @Override
    protected String createFieldName()
    {
        return "THE_CONSTANT";
    }


    @Override
    protected String createFieldDeclaration(String pName)
    {
        return pName + ',';
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "enum X {";
    }
}
