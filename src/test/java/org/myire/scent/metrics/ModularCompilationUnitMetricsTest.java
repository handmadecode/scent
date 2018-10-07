/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertSame;


/**
 * Unit tests for {@code ModularCompilationUnitMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ModularCompilationUnitMetricsTest extends CodeElementMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null
     * {@code ModuleDeclarationMetrics}.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullModuleDeclarationMetrics()
    {
        new ModularCompilationUnitMetrics("module-info.java", null);
    }


    /**
     * The {@code getModule} method should return the {@code ModuleDeclarationMetrics} passed to the
     * constructor.
     */
    @Test
    public void getModuleReturnsValuePassedToCtor()
    {
        // Given
        ModuleDeclarationMetrics aModule = new ModuleDeclarationMetrics("x", false);

        // When
        ModularCompilationUnitMetrics aMetrics = new ModularCompilationUnitMetrics("", aModule);

        // Then
        assertSame(aModule, aMetrics.getModule());
    }


    @Override
    protected CodeElementMetrics createInstance(String pName)
    {
        return new ModularCompilationUnitMetrics(pName, new ModuleDeclarationMetrics("x", false));
    }
}
