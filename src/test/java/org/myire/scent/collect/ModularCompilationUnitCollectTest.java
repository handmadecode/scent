/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;
import java.util.Iterator;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.ModularCompilationUnitMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstModularCompilationUnit;


/**
 * Unit tests for parsing a modular compilation unit and collecting the source code metrics related
 * it.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ModularCompilationUnitCollectTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null compilation
     * unit.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullCompilationUnit()
    {
        new ModularCompilationUnitMetricsCollector(null, "");
    }


    /**
     * The constructor should throw a {@code NullPointerException} when passed a null name.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullName()
    {
        new ModularCompilationUnitMetricsCollector(new CompilationUnit(), null);
    }


    /**
     * The constructor should throw an {@code IllegalArgumentException} when passed a compilation
     * unit without a module declaration.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsForCompilationUnitWithoutModule()
    {
        new ModularCompilationUnitMetricsCollector(new CompilationUnit(), "");
    }


    /**
     * A modular compilation unit should be collected into a {@code ModularCompilationUnitMetrics}
     * with the name passed to the {@code collect} method.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void modularCompilationUnitIsCollected() throws ParseException
    {
        // Given
        String aName = "module-info.java";

        // When
        JavaMetrics aMetrics = collect(aName, "module org.acme.util { exports org.acme.util; }");

        // Then
        assertEquals(aName, getFirstModularCompilationUnit(aMetrics).getName());
    }


    /**
     * A modular compilation unit should not cause metrics for the default package to be created.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void modularCompilationUnitIsNotAssignedToPackage() throws ParseException
    {
        // When
        JavaMetrics aMetrics = collect("module-info.java", "module mod { requires java.sql; }");

        // Then
        assertFalse(aMetrics.getPackages().iterator().hasNext());
    }


    /**
     * Collecting multiple modular compilation units should result in the same number of
     * {@code ModularCompilationUnitMetrics} with the names passed to the {@code collect} method.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void compilationUnitsAreCollectedInSeparateMetrics() throws ParseException
    {
        // Given
        String aName1 = "module-info.java";
        String aName2 = "module-info2.java";
        JavaMetricsCollector aCollector = new JavaMetricsCollector();

        // When
        aCollector.collect(aName1, "module x { exports x.a; }");
        aCollector.collect(aName2, "module y { exports y.a; }");

        // Then
        JavaMetrics aMetrics = aCollector.getCollectedMetrics();
        assertEquals(2, aMetrics.getNumModularCompilationUnits());
        Iterator<ModularCompilationUnitMetrics> aIterator = aMetrics.getModularCompilationUnits().iterator();
        assertEquals(aName1, aIterator.next().getName());
        assertEquals(aName2, aIterator.next().getName());
        assertFalse(aIterator.hasNext());
    }


    /**
     * A modular compilation unit header in the form of a block comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentHeaderIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "/*",
            " * File header",
            " */",
            "",
            "module z {exports y;}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(11, aComments.getBlockCommentsLength());
    }


    /**
     * A modular compilation unit header in the form of a single line comment should be collected as
     * a {@code CommentMetrics} within the compilation unit's {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentHeaderIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "// Copyright (c) 2018",
            "",
            "module z {exports y;}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(18, aComments.getLineCommentsLength());
    }


    /**
     * A modular compilation unit header in the form of several single line comments should be
     * collected as a {@code CommentMetrics} within the compilation unit's
     * {@code ModularCompilationUnitMetrics}.
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
            "module w {requires q;}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(2, aComments.getNumLineComments());
        assertEquals(27, aComments.getLineCommentsLength());
    }


    /**
     * A modular compilation unit footer in the form of a block comment should be collected as a
     * {@code CommentMetrics} within the compilation unit's {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module xxx {requires yyy;}",
            "/*",
            " The footer",
            "",
            " */"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
        assertEquals(10, aComments.getBlockCommentsLength());
    }


    /**
     * A modular compilation unit footer in the form of a single line comment should be collected as
     * a {@code CommentMetrics} within the compilation unit's {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module mod {exports nothing;}",
            "//Footer"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(6, aComments.getLineCommentsLength());
    }


    /**
     * A modular compilation unit header in the form of several single line comments should be
     * collected as a {@code CommentMetrics} within the compilation unit's
     * {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentFooterIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module m.o.d {requires a.lot;}",
            "// Footer 1",
            "// Footer 2",
            "// Footer 3",
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(3, aComments.getNumLineComments());
        assertEquals(24, aComments.getLineCommentsLength());
    }


    /**
     * A comment for an import should be collected as a {@code CommentMetrics} within the
     * compilation unit's {@code ModularCompilationUnitMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void importCommentIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "// This is an import",
            "import java.sql.Driver;",
            "module s.q.l {uses java.sql.Driver;}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstModularCompilationUnit(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(17, aComments.getLineCommentsLength());
    }
}
