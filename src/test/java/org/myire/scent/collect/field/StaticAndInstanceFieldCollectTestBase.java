/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.field;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.PackageMetrics;

import static org.myire.scent.collect.CollectTestUtil.collect;
import static org.myire.scent.collect.CollectTestUtil.getFirstType;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for static and
 * instance fields.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class StaticAndInstanceFieldCollectTestBase extends FieldCollectTestBase
{
    /**
     * Multiple fields declared in the same field declaration should be collected in separate
     * {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multipleFieldsAreCollectedInSeparateMetrics() throws ParseException
    {
        // Given
        String aFieldNameBase = createFieldName();
        String aFieldName1 = aFieldNameBase + '1';
        String aFieldName2 = aFieldNameBase + '2';
        String aFieldName3 = aFieldNameBase + '3';
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createFieldDeclaration(aFieldName1 + ',' + aFieldName2 + ',' + aFieldName3),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        Iterator<FieldMetrics> aFields = getFirstType(aMetrics).getFields().iterator();
        assertEquals(aFieldName1, aFields.next().getName());
        assertEquals(aFieldName2, aFields.next().getName());
        assertEquals(aFieldName3, aFields.next().getName());
    }


    /**
     * The comments of a field declaration with multiple variables should be collected in each
     * variable's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void commentsInMultiDeclarationAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "// First field with two",
                "// line comments",
                createModifiersAndType() + " f1,",
                "/* Second field with ",
                "   block comment */",
                "f2,",
                "/**",
                " * Third field with JavaDoc.",
                " */",
                "f3;",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        Iterator<FieldMetrics> aFields = getFirstType(aMetrics).getFields().iterator();
        CommentMetrics aComments = aFields.next().getComments();
        assertEquals(2, aComments.getNumLineComments());
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
    }


    /**
     * The comment belonging to the field type in a field declaration with multiple variables should
     * be collected in the comments metrics of the first variable.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void typeCommentInInMultiDeclarationIsCollectedForFirstField() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "class X {",
                "/**",
                " * JavaDoc for the type, should be collected for the first field.",
                " */",
                createModifiersAndType(),
                "/* First field with */",
                "/* two block comments */",
                "f1,",
                "// Second field with",
                "// two line comments",
                "f2;",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        Iterator<FieldMetrics> aFields = getFirstType(aMetrics).getFields().iterator();
        CommentMetrics aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
        aComments = aFields.next().getComments();
        assertEquals(2, aComments.getNumLineComments());
        assertEquals(0, aComments.getNumJavaDocComments());
        assertEquals(0, aComments.getNumJavaDocLines());
    }


    @Override
    protected String createFieldDeclaration(String pName)
    {
        return createModifiersAndType() + ' ' + pName + ';';
    }


    @Override
    protected String createTypeDeclarationStart()
    {
        return "class X {";
    }


    /**
     * Create the modifiers and type part of a field declaration being tested by this class.
     *
     * @return  The modifiers and type of the field declaration.
     */
    abstract protected String createModifiersAndType();
}
