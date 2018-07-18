/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.PackageMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;
import static org.myire.scent.util.CollectTestUtil.toSourceString;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for methods of any
 * kind.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class MethodCollectTestBase
{
    /**
     * A method should be collected as a {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void methodIsCollected() throws ParseException
    {
        // Given
        String aName = createMethodName();

        // When
        Iterable<PackageMetrics> aMetrics = collect(createMethodDeclarationInsideType(aName));

        // Then
        assertEquals(aName, getFirstMethod(aMetrics).getName());
    }


    /**
     * A method declaration should be collected as a {@code MethodMetrics} with the correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void methodMetricsHasTheCorrectKind() throws ParseException
    {
        // When
        Iterable<PackageMetrics> aMetrics = collect(createMethodDeclarationInsideType(createMethodName()));

        // Then
        assertEquals(getMethodKind(), getFirstMethod(aMetrics).getKind());
    }


    /**
     * A block comment for a method should be collected in the {@code CommentMetrics} of the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleBlockCommentForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "/*",
                " * Comment about the method",
                " * spans four lines",
                " */",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
    }


    /**
     * Multiple block comments for a method should be collected in the {@code CommentMetrics} of the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiBlockCommentForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "/* Comment about the method */",
                "/* is separated into two single-line block comments */",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
    }


    /**
     * A single line comment for a method should be collected in the {@code CommentMetrics} of the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "// Comment about the method",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * Multiple single line comments for a method should be collected in the {@code CommentMetrics}
     * of the method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "// Comment about the method",
                "// spans three lines",
                "// and is quite verbose",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(3, aComments.getNumLineComments());
    }


    /**
     * A trailing line comment for a method should be collected in the {@code CommentMetrics} of the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void trailingLineCommentForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclaration(createMethodName()) + "// Comment about the method",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A JavaDoc comment for a method should be collected in the {@code CommentMetrics} of the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void javadocForMethodIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "/**",
                " * A Method.",
                " */",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
    }


    /**
     * A method having a block comment, a line comment, and a JavaDoc comment should have all those
     * comments collected in the {@code CommentMetrics} of the method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multipleCommentsForMethodAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                "/* Method block comment spanning",
                "   three",
                "   lines. */",
                "/**",
                " * Method javadoc.",
                " */",
                "// Method line comment",
                createMethodDeclaration(createMethodName()),
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
    }


    /**
     * Get the {@code MethodMetrics.Kind} for the methods being tested by this class.
     *
     * @return  The method kind.
     */
    abstract protected MethodMetrics.Kind getMethodKind();

    /**
     * Create a typical name for the kind of method being tested. The returned name should be the
     * one expected to be collected as the method name when the method is constructed by calling
     * {@link #createMethodDeclaration}.
     *
     * @return  A typical method name.
     */
    abstract protected String createMethodName();

    /**
     * Create a declaration of the method being tested by this class.
     *
     * @param pName The name of the method on the form it is expected to be collected.
     *
     * @return  A method declaration with the specified name.
     */
    abstract protected String createMethodDeclaration(String pName);

    /**
     * Create the start of a type declaration suitable to contain the kind of method being tested by
     * this class. The start of the type declaration ends at the point where the first method should
     * be placed.
     *
     * @return  The start of a type declaration.
     */
    abstract protected String createTypeDeclarationStart();


    /**
     * Create a type declaration containing a single method declaration of the kind being tested by
     * this class.
     *
     * @param pName The name of the method.
     *
     * @return  A string with the type and method declaration.
     */
    private String createMethodDeclarationInsideType(String pName)
    {
        return toSourceString(
                new String[]{
                        createTypeDeclarationStart(),
                        createMethodDeclaration(pName),
                        "}"
                }
        );
    }
}
