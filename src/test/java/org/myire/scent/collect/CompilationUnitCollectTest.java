/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstCompilationUnit;
import static org.myire.scent.util.CollectTestUtil.getFirstInnerType;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;
import static org.myire.scent.util.CollectTestUtil.getFirstPackage;
import static org.myire.scent.util.CollectTestUtil.getFirstType;


/**
 * Unit tests for parsing a compilation unit and collecting the source code metrics related it.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class CompilationUnitCollectTest
{
    /**
     * A parsed compilation unit should be collected into a {@code CompilationUnitMetrics} with the
     * name passed to the {@code collect} method.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void compilationUnitIsCollected() throws ParseException
    {
        // Given
        String aName = "Test.java";

        // When
        JavaMetrics aMetrics = collect(aName, "class Test {}");

        // Then
        assertEquals(aName, getFirstCompilationUnit(aMetrics).getName());
    }


    /**
     * Parsing multiple compilation units should result in the same number of
     * {@code CompilationUnitMetrics} with the names passed to the {@code collect} method.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void compilationUnitsAreCollectedInSeparateMetrics() throws ParseException
    {
        // Given
        String aName1 = "What.java";
        String aName2 = "Who.java";
        String aName3 = "Why.java";
        JavaMetricsCollector aParser = new JavaMetricsCollector();

        // When
        aParser.collect(aName1, "class What {}");
        aParser.collect(aName2, "class Who {}");
        aParser.collect(aName3, "class Why {}");

        // Then
        PackageMetrics aPackage = getFirstPackage(aParser.getCollectedMetrics());
        assertEquals(3, aPackage.getNumCompilationUnits());
        Iterator<CompilationUnitMetrics> aIterator = aPackage.getCompilationUnits().iterator();
        assertEquals(aName1, aIterator.next().getName());
        assertEquals(aName2, aIterator.next().getName());
        assertEquals(aName3, aIterator.next().getName());
        assertFalse(aIterator.hasNext());
    }


    /**
     * A compilation unit containing only a module declaration should not be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void moduleDeclarationIsNotCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module org.acme.util {",
            "  requires java.sql;",
            "  exports org.acme.util;",
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        assertFalse(aMetrics.getPackages().iterator().hasNext());
    }


    /**
     * A compilation unit header in the form of a block comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentHeaderIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "/*",
                " * Copyright (c) 2016",
                " */",
                "package x.y.z;",
                "class X{}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
    }


    /**
     * A compilation unit header in the form of a single line comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentHeaderIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "// Copyright (c) 2016",
                "package x.y.z;",
                "interface Z {}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A compilation unit header in the form of several single line comments should be collected as
     * a {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentHeaderIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "// Copyright (c)",
                "// All rights etc",
                "",
                "package x.y.z;",
                "enum Q {}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(2, aComments.getNumLineComments());
    }


    /**
     * A compilation unit footer in the form of a block comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "class X {}",
                "/*",
                " The footer",
                "",
                " */"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
    }


    /**
     * A compilation unit footer in the form of a single line comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "class X {}",
                "//Footer"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A compilation unit header in the form of several single line comments should be collected as
     * a {@code CommentMetrics} within the compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "class X {}",
                "// Footer 1",
                "// Footer 2",
                "// Footer 3",
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(3, aComments.getNumLineComments());
    }


    /**
     * A comment for an import should be collected as a {@code CommentMetrics} within the
     * compilation unit's {@code CompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void importCommentIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "package x.y.z;",
                "// This is an import",
                "import java.util.Random;",
                "class X {}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A compilation unit containing only comments and a package declaration should be collected in
     * the {@code CommentMetrics} associated with the package, not the compilation unit.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void commentsInEmptyCompilationUnitAreCollectedForPackage() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "// Line comment for package",
            "/**",
            " * Package JavaDoc.",
            " */",
            "package x.y.z;",
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        PackageMetrics aPackageMetrics = getFirstPackage(aMetrics);
        CommentMetrics aComments = aPackageMetrics.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(24, aComments.getLineCommentsLength());
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(16, aComments.getJavaDocCommentsLength());
        aComments = getFirstCompilationUnit(aPackageMetrics).getComments();
        assertTrue(aComments.isEmpty());
    }


    /**
     * A compilation unit with comments attached to all kinds of code elements should have all
     * comments collected in the correct code element metrics.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedComments() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "/*",
                " * Three lines of file header",
                " */",
                "package x.y.z;",
                "/**",
                " * Four lines of JavaDoc for",
                " * the class",
                " */",
                "class X {",
                "    // Single line comment for a field",
                "    int f1;",
                "    /* Two-line block comment ",
                "        for a field */",
                "    int f2;",
                "    /**",
                "     * A method JavaDoc with",
                "     * five lines.",
                "     * @return An int.",
                "     */",
                "    int aMethod() { return 1;}" +
                "    /**",
                "     * JavaDoc for inner enum.",
                "     *<code>",
                "     * x=1;",
                "     *</code>",
                "     * Seven lines.",
                "     */",
                "    static enum WindowState { ",
                "        // Single line comment for enum constant",
                "        OPEN, ",
                "        /** JavaDoc for enum constant. */",
                "        CLOSED",
                "    }",
                "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aMetrics);
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());

        TypeMetrics aClass = getFirstType(aCompilationUnit);
        aComments = aClass.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(4, aComments.getNumJavaDocLines());

        Iterator<FieldMetrics> aFields = aClass.getFields().iterator();
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumLineComments());
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());

        aComments = getFirstMethod(aClass).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(5, aComments.getNumJavaDocLines());

        TypeMetrics aInnerType = getFirstInnerType(aClass);
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(7, aComments.getNumJavaDocLines());

        aFields = aInnerType.getFields().iterator();
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumLineComments());
        aComments = aFields.next().getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(1, aComments.getNumJavaDocLines());
    }
}
