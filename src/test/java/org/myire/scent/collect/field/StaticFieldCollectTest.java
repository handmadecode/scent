/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.field;

import org.myire.scent.metrics.FieldMetrics;


/**
 * Unit tests related to parsing and collecting metrics for static fields.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class StaticFieldCollectTest extends StaticAndInstanceFieldCollectTestBase
{
    @Override
    protected FieldMetrics.Kind getFieldKind()
    {
        return FieldMetrics.Kind.STATIC_FIELD;
    }


    @Override
    protected String createFieldName()
    {
        return "cField";
    }


    @Override
    protected String createModifiersAndType()
    {
        return "static int";
    }
}
