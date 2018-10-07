/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.field;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstField;
import static org.myire.scent.util.CollectTestUtil.toSourceString;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for fields of any
 * kind.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class FieldCollectTestBase
{
    /**
     * A field member should be collected as a {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fieldIsCollected() throws ParseException
    {
        // Given
        String aName = createFieldName();

        // When
        JavaMetrics aMetrics = collect(createFieldDeclarationInsideType(aName));

        // Then
        assertEquals(aName, getFirstField(aMetrics).getName());
    }


    /**
     * A field declaration should be collected as a {@code FieldMetrics} with the correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fieldMetricsHasTheCorrectKind() throws ParseException
    {
        // When
        JavaMetrics aMetrics = collect(createFieldDeclarationInsideType(createFieldName()));

        // Then
        assertEquals(getFieldKind(), getFirstField(aMetrics).getKind());
    }


    /**
     * A block comment for a field should be collected in the {@code CommentMetrics} of the field's
     * {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleBlockCommentForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            createTypeDeclarationStart(),
            "/* Comment about the field",
            "   spans two lines */",
            createFieldDeclaration(createFieldName()),
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
    }


    /**
     * Multiple block comment for a field should be collected in the {@code CommentMetrics} of the
     * field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiBlockCommentForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "/* Comment about the field */",
                "/* is separated into two block comments */",
                createFieldDeclaration(createFieldName()),
                "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
    }


    /**
     * A single line comment for a field should be collected in the {@code CommentMetrics} of the
     * field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            createTypeDeclarationStart(),
            "// Comment about the field",
            createFieldDeclaration(createFieldName()),
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * Multiple single line comment for a field should be collected in the {@code CommentMetrics}
     * of the within the field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            createTypeDeclarationStart(),
            "// Comment about the field",
            "// spans three lines",
            "// and is somewhat verbose",
            createFieldDeclaration(createFieldName()),
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(3, aComments.getNumLineComments());
    }


    /**
     * A trailing line comment for a field should be collected in the {@code CommentMetrics} of the
     * field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void trailingLineCommentForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createFieldDeclaration(createFieldName()) + "// Comment about the field",
                "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A JavaDoc comment for a field should be collected in the {@code CommentMetrics} of the
     * field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void javadocForFieldIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            createTypeDeclarationStart(),
            "/** A field. */",
            createFieldDeclaration(createFieldName()),
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(1, aComments.getNumJavaDocLines());
    }


    /**
     * A field having a block comment, a line comment, and a JavaDoc comment should have all those
     * comments collected in the {@code CommentMetrics} of the field's {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multipleCommentsForFieldAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "// Field line comment",
                "/**",
                " * Field javadoc.",
                " */",
                "/* Field block comment spanning",
                "   two lines. */",
                createFieldDeclaration(createFieldName()),
                "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstField(aMetrics).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
    }


    /**
     * Get the {@code FieldMetrics.Kind} for the fields being tested by this class.
     *
     * @return  The field kind.
     */
    abstract protected FieldMetrics.Kind getFieldKind();

    /**
     * Create a typical name for the kind of field being tested. The returned name should be the
     * one expected to be collected as the field name when the field is constructed by calling
     * {@link #createFieldDeclaration}.
     *
     * @return  A typical field name.
     */
    abstract protected String createFieldName();

    /**
     * Create a declaration of the field being tested by this class.
     *
     * @param pName The name of the field on the form it is expected to be collected.
     *
     * @return  A field declaration with the specified name.
     */
    abstract protected String createFieldDeclaration(String pName);

    /**
     * Create the start of a type declaration suitable to contain the kind of field being tested by
     * this class. The start of the type declaration ends at the point where the first field should
     * be placed.
     *
     * @return  The start of a type declaration.
     */
    abstract protected String createTypeDeclarationStart();


    /**
     * Create a type declaration containing a single field declaration of the kind being tested by
     * this class.
     *
     * @param pName The name of the field.
     *
     * @return  A string with the type and field declaration.
     */
    private String createFieldDeclarationInsideType(String pName)
    {
        return toSourceString(
            new String[]{
                createTypeDeclarationStart(),
                createFieldDeclaration(pName),
                "}"
            }
        );
    }
}
