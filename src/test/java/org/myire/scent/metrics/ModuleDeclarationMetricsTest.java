/*
 * Copyright 2018-2019 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;


/**
 * Unit tests for {@code ModuleDeclarationMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ModuleDeclarationMetricsTest extends CodeElementMetricsTest
{
    /**
     * The {@code isOpen} method should return the value passed to the constructor.
     */
    @Test
    public void isOpenReturnsValuePassedToCtor()
    {
        // When
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("x", true);

        // Then
        assertTrue(aMetrics.isOpen());

        // When
        aMetrics = new ModuleDeclarationMetrics("x", false);

        // Then
        assertFalse(aMetrics.isOpen());
    }


    /**
     * A newly created {@code ModuleDeclarationMetrics} should have zero statements.
     */
    @Test
    public void newInstanceHasZeroStatements()
    {
        // When
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("m.o.d", false);

        // Then
        assertEquals(0, aMetrics.getNumRequiresDirectives());
        assertEquals(0, aMetrics.getNumExportsDirectives());
        assertEquals(0, aMetrics.getNumUsesDirectives());
        assertEquals(0, aMetrics.getNumProvidesDirectives());
        assertEquals(0, aMetrics.getNumOpensDirectives());
    }


    /**
     * Calling {@code add(ModuleRequiresStmt)} should increase the {@code requires} statements count.
     */
    @Test
    public void addRequiresIncreasesCount()
    {
        // Given
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("se.u.l8r", false);

        // When
        aMetrics.add(new ModuleRequiresDirective());

        // Then
        assertEquals(1, aMetrics.getNumRequiresDirectives());
        assertEquals(0, aMetrics.getNumExportsDirectives());
        assertEquals(0, aMetrics.getNumUsesDirectives());
        assertEquals(0, aMetrics.getNumProvidesDirectives());
        assertEquals(0, aMetrics.getNumOpensDirectives());
    }


    /**
     * Calling {@code add(ModuleExportsStmt)} should increase the {@code exports} statements count.
     */
    @Test
    public void addExportsIncreasesCount()
    {
        // Given
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("zzz", false);

        // When
        aMetrics.add(new ModuleExportsDirective());

        // Then
        assertEquals(0, aMetrics.getNumRequiresDirectives());
        assertEquals(1, aMetrics.getNumExportsDirectives());
        assertEquals(0, aMetrics.getNumUsesDirectives());
        assertEquals(0, aMetrics.getNumProvidesDirectives());
        assertEquals(0, aMetrics.getNumOpensDirectives());
    }


    /**
     * Calling {@code add(ModuleUsesStmt)} should increase the {@code uses} statements count.
     */
    @Test
    public void addUsesIncreasesCount()
    {
        // Given
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("mod", true);

        // When
        aMetrics.add(new ModuleUsesDirective());

        // Then
        assertEquals(0, aMetrics.getNumRequiresDirectives());
        assertEquals(0, aMetrics.getNumExportsDirectives());
        assertEquals(1, aMetrics.getNumUsesDirectives());
        assertEquals(0, aMetrics.getNumProvidesDirectives());
        assertEquals(0, aMetrics.getNumOpensDirectives());
    }


    /**
     * Calling {@code add(ModuleProvidesStmt)} should increase the {@code provides} statements count.
     */
    @Test
    public void addProvidesIncreasesCount()
    {
        // Given
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("my-mod", true);

        // When
        aMetrics.add(new ModuleProvidesDirective());

        // Then
        assertEquals(0, aMetrics.getNumRequiresDirectives());
        assertEquals(0, aMetrics.getNumExportsDirectives());
        assertEquals(0, aMetrics.getNumUsesDirectives());
        assertEquals(1, aMetrics.getNumProvidesDirectives());
        assertEquals(0, aMetrics.getNumOpensDirectives());
    }


    /**
     * Calling {@code add(ModuleOpensStmt)} should increase the {@code opens} statements count.
     */
    @Test
    public void addOpensIncreasesCount()
    {
        // Given
        ModuleDeclarationMetrics aMetrics = new ModuleDeclarationMetrics("mm", false);

        // When
        aMetrics.add(new ModuleOpensDirective());

        // Then
        assertEquals(0, aMetrics.getNumRequiresDirectives());
        assertEquals(0, aMetrics.getNumExportsDirectives());
        assertEquals(0, aMetrics.getNumUsesDirectives());
        assertEquals(0, aMetrics.getNumProvidesDirectives());
        assertEquals(1, aMetrics.getNumOpensDirectives());
    }


    @Override
    protected CodeElementMetrics createInstance(String pName)
    {
        return new ModuleDeclarationMetrics(pName, false);
    }
}
