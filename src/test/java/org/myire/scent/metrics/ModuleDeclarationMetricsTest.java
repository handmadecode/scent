/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ast.modules.ModuleExportsStmt;
import com.github.javaparser.ast.modules.ModuleOpensStmt;
import com.github.javaparser.ast.modules.ModuleProvidesStmt;
import com.github.javaparser.ast.modules.ModuleRequiresStmt;
import com.github.javaparser.ast.modules.ModuleUsesStmt;


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
        assertEquals(0, aMetrics.getNumRequiresStatements());
        assertEquals(0, aMetrics.getNumExportsStatements());
        assertEquals(0, aMetrics.getNumUsesStatements());
        assertEquals(0, aMetrics.getNumProvidesStatements());
        assertEquals(0, aMetrics.getNumOpensStatements());
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
        aMetrics.add(new ModuleRequiresStmt());

        // Then
        assertEquals(1, aMetrics.getNumRequiresStatements());
        assertEquals(0, aMetrics.getNumExportsStatements());
        assertEquals(0, aMetrics.getNumUsesStatements());
        assertEquals(0, aMetrics.getNumProvidesStatements());
        assertEquals(0, aMetrics.getNumOpensStatements());
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
        aMetrics.add(new ModuleExportsStmt());

        // Then
        assertEquals(0, aMetrics.getNumRequiresStatements());
        assertEquals(1, aMetrics.getNumExportsStatements());
        assertEquals(0, aMetrics.getNumUsesStatements());
        assertEquals(0, aMetrics.getNumProvidesStatements());
        assertEquals(0, aMetrics.getNumOpensStatements());
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
        aMetrics.add(new ModuleUsesStmt());

        // Then
        assertEquals(0, aMetrics.getNumRequiresStatements());
        assertEquals(0, aMetrics.getNumExportsStatements());
        assertEquals(1, aMetrics.getNumUsesStatements());
        assertEquals(0, aMetrics.getNumProvidesStatements());
        assertEquals(0, aMetrics.getNumOpensStatements());
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
        aMetrics.add(new ModuleProvidesStmt());

        // Then
        assertEquals(0, aMetrics.getNumRequiresStatements());
        assertEquals(0, aMetrics.getNumExportsStatements());
        assertEquals(0, aMetrics.getNumUsesStatements());
        assertEquals(1, aMetrics.getNumProvidesStatements());
        assertEquals(0, aMetrics.getNumOpensStatements());
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
        aMetrics.add(new ModuleOpensStmt());

        // Then
        assertEquals(0, aMetrics.getNumRequiresStatements());
        assertEquals(0, aMetrics.getNumExportsStatements());
        assertEquals(0, aMetrics.getNumUsesStatements());
        assertEquals(0, aMetrics.getNumProvidesStatements());
        assertEquals(1, aMetrics.getNumOpensStatements());
    }


    @Override
    protected CodeElementMetrics createInstance(String pName)
    {
        return new ModuleDeclarationMetrics(pName, false);
    }
}
