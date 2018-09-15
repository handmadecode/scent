/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.ModularCompilationUnitMetrics;
import org.myire.scent.metrics.ModuleDeclarationMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.collectFromResource;
import static org.myire.scent.util.CollectTestUtil.getFirstModularCompilationUnit;
import static org.myire.scent.util.CollectTestUtil.getFirstModuleDeclaration;


/**
 * Unit tests for parsing a module declaration and collecting the source code metrics related it.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ModuleDeclarationCollectTest
{
    /**
     * A module declaration's name should be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void moduleNameIsCollected() throws ParseException
    {
        // Given
        String aModuleName = "org.acme.world.domination";
        String[] aSourceLines = {
            "module " + aModuleName + " {",
            "  requires org.obedience;",
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        assertEquals(aModuleName, getFirstModuleDeclaration(aMetrics).getName());
    }


    /**
     * A open module declaration should be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void openModuleIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "open module some.module {",
            "  requires java.sql;",
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        assertTrue(getFirstModuleDeclaration(aMetrics).isOpen());
    }


    /**
     * A module declaration that is not open should be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void notOpenModuleIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module some.module {",
            "  requires java.sql;",
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        assertFalse(getFirstModuleDeclaration(aMetrics).isOpen());
    }


    /**
     * A module declaration's statements should be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void moduleStatementsAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
            "module an.other.module {",
            "  requires x.y;",
            "  requires q.w;",
            "  requires z.m;",
            "  requires z.n;",
            "  requires z.o;",
            "  exports an.other.module.a;",
            "  exports an.other.module.b;",
            "  exports an.other.module.c;",
            "  exports an.other.module.d;",
            "  uses org.nifty.FirstService;",
            "  uses org.nifty.SecondService;",
            "  uses org.nifty.ThirdService;",
            "  provides org.nifty.FourthService with an.other.module.FourthServiceImpl;",
            "  provides org.nifty.FifthService with an.other.module.FifthServiceImpl;",
            "  opens an.other.module.z;",
            "}"
        };

        // When
        JavaMetrics aMetrics = collect(aSourceLines);

        // Then
        ModuleDeclarationMetrics aModule = getFirstModuleDeclaration(aMetrics);
        assertEquals(aModule.getNumRequiresStatements(), 5);
        assertEquals(aModule.getNumExportsStatements(), 4);
        assertEquals(aModule.getNumUsesStatements(), 3);
        assertEquals(aModule.getNumProvidesStatements(), 2);
        assertEquals(aModule.getNumOpensStatements(), 1);
    }


    /**
     * A module declaration should have its statements and comments collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/module-info.java";

        // When
        JavaMetrics aMetrics = collectFromResource(aResourceName);

        // Then
        ModularCompilationUnitMetrics aModularCompilationUnit = getFirstModularCompilationUnit(aMetrics);
        CommentMetrics aComments = aModularCompilationUnit.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(60, aComments.getLineCommentsLength());
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
        assertEquals(80, aComments.getBlockCommentsLength());
        assertEquals(0, aComments.getNumJavaDocComments());
        assertEquals(0, aComments.getNumJavaDocLines());
        assertEquals(0, aComments.getJavaDocCommentsLength());

        ModuleDeclarationMetrics aModule = aModularCompilationUnit.getModule();
        assertEquals("org.an.iz", aModule.getName());
        assertFalse(aModule.isOpen());
        assertEquals(2, aModule.getNumRequiresStatements());
        assertEquals(3, aModule.getNumExportsStatements());
        assertEquals(0, aModule.getNumUsesStatements());
        assertEquals(1, aModule.getNumProvidesStatements());
        assertEquals(0, aModule.getNumOpensStatements());

        aComments = aModule.getComments();
        assertEquals(3, aComments.getNumLineComments());
        assertEquals(83, aComments.getLineCommentsLength());
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(46, aComments.getBlockCommentsLength());
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(1, aComments.getNumJavaDocLines());
        assertEquals(35, aComments.getJavaDocCommentsLength());
    }
}
