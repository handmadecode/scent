/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.field;

import org.myire.scent.metrics.FieldMetrics;


/**
 * Unit tests related to parsing and collecting metrics for annotation type elements.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class AnnotationTypeElementCollectTest extends FieldCollectTestBase
{
    @Override
    protected FieldMetrics.Kind getFieldKind()
    {
        return FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT;
    }


    @Override
    protected String createFieldName()
    {
        return "someAnnotationElement";
    }


    @Override
    protected String createFieldDeclaration(String pName)
    {
        return "int " + pName + "();";
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "@interface AnyAnnotation {";
    }
}
