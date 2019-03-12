/*
 * Copyright 2018-2019 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;


/**
 * Source code metrics for a module declaration.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class ModuleDeclarationMetrics extends CodeElementMetrics
{
    private final boolean fIsOpen;

    private int fNumRequiresDirectives;
    private int fNumExportsDirectives;
    private int fNumProvidesDirectives;
    private int fNumUsesDirectives;
    private int fNumOpensDirectives;


    /**
     * Create a new {@code ModuleDeclarationMetrics}.
     *
     * @param pName     The name of the module.
     * @param pIsOpen   True if the module is an open module, false if not.
     *
     * @throws NullPointerException if {@code pName} is null.
     */
    public ModuleDeclarationMetrics(@Nonnull String pName, boolean pIsOpen)
    {
        super(pName);
        fIsOpen = pIsOpen;
    }


    /**
     * Is the module declared as open?
     *
     * @return  True if the module is open, false if not.
     */
    public boolean isOpen()
    {
        return fIsOpen;
    }


    /**
     * Get the number of {@code requires} directives added to this instance.
     *
     * @return  The number of {@code requires} directives.
     */
    public int getNumRequiresDirectives()
    {
        return fNumRequiresDirectives;
    }


    /**
     * Get the number of {@code exports} directives added to this instance.
     *
     * @return  The number of {@code exports} directives.
     */
    public int getNumExportsDirectives()
    {
        return fNumExportsDirectives;
    }


    /**
     * Get the number of {@code provides} directives added to this instance.
     *
     * @return  The number of {@code provides} directives.
     */
    public int getNumProvidesDirectives()
    {
        return fNumProvidesDirectives;
    }


    /**
     * Get the number of {@code uses} directives added to this instance.
     *
     * @return  The number of {@code uses} directives.
     */
    public int getNumUsesDirectives()
    {
        return fNumUsesDirectives;
    }


    /**
     * Get the number of {@code opens} directives added to this instance.
     *
     * @return  The number of {@code opens} directives.
     */
    public int getNumOpensDirectives()
    {
        return fNumOpensDirectives;
    }


    /**
     * Add metrics of a {@code requires} directive.
     *
     * @param pDirective    The directive.
     *
     * @throws NullPointerException if {@code pDirective} is null.
     */
    public void add(@Nonnull ModuleRequiresDirective pDirective)
    {
        requireNonNull(pDirective);
        fNumRequiresDirectives++;
    }


    /**
     * Add metrics of an {@code exports} directive.
     *
     * @param pDirective    The directive.
     *
     * @throws NullPointerException if {@code pDirective} is null.
     */
    public void add(@Nonnull ModuleExportsDirective pDirective)
    {
        requireNonNull(pDirective);
        fNumExportsDirectives++;
    }


    /**
     * Add metrics of a {@code provides} directive.
     *
     * @param pDirective    The directive.
     *
     * @throws NullPointerException if {@code pDirective} is null.
     */
    public void add(@Nonnull ModuleProvidesDirective pDirective)
    {
        requireNonNull(pDirective);
        fNumProvidesDirectives++;
    }


    /**
     * Add metrics of a {@code uses} directive.
     *
     * @param pDirective    The directive.
     *
     * @throws NullPointerException if {@code pDirective} is null.
     */
    public void add(@Nonnull ModuleUsesDirective pDirective)
    {
        requireNonNull(pDirective);
        fNumUsesDirectives++;
    }


    /**
     * Add metrics of an {@code opens} directive.
     *
     * @param pDirective    The directive.
     *
     * @throws NullPointerException if {@code pDirective} is null.
     */
    public void add(@Nonnull ModuleOpensDirective pDirective)
    {
        requireNonNull(pDirective);
        fNumOpensDirectives++;
    }
}
