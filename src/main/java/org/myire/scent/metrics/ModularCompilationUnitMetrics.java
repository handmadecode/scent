/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Source code metrics for a modular compilation unit. These metrics contain metrics for the module
 * declaration and metrics for any comments associated with the compilation unit itself, such as a
 * file header.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class ModularCompilationUnitMetrics extends CodeElementMetrics
{
    private final ModuleDeclarationMetrics fModule;


    /**
     * Create a new {@code ModularCompilationUnitMetrics}.
     *
     * @param pName     The name of the compilation unit.
     * @param pModule   The metrics for the compilation unit's module declaration.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public ModularCompilationUnitMetrics(
        @Nonnull String pName,
        @Nonnull ModuleDeclarationMetrics pModule)
    {
        super(pName);
        fModule = requireNonNull(pModule);
    }


    /**
     * Get the metrics for the module declaration enclosed within the compilation unit.
     *
     * @return  The module declaration's metrics, never null.
     */
    @Nonnull
    public ModuleDeclarationMetrics getModule()
    {
        return fModule;
    }
}
