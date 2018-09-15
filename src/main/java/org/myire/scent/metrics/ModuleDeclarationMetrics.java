/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.modules.ModuleExportsStmt;
import com.github.javaparser.ast.modules.ModuleOpensStmt;
import com.github.javaparser.ast.modules.ModuleProvidesStmt;
import com.github.javaparser.ast.modules.ModuleRequiresStmt;
import com.github.javaparser.ast.modules.ModuleUsesStmt;


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

    private int fNumRequiresStatements;
    private int fNumExportsStatements;
    private int fNumProvidesStatements;
    private int fNumUsesStatements;
    private int fNumOpensStatements;


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
     * Get the number of {@code requires} statements added to this instance.
     *
     * @return  The number of {@code requires} statements.
     */
    public int getNumRequiresStatements()
    {
        return fNumRequiresStatements;
    }


    /**
     * Get the number of {@code exports} statements added to this instance.
     *
     * @return  The number of {@code exports} statements.
     */
    public int getNumExportsStatements()
    {
        return fNumExportsStatements;
    }


    /**
     * Get the number of {@code provides} statements added to this instance.
     *
     * @return  The number of {@code provides} statements.
     */
    public int getNumProvidesStatements()
    {
        return fNumProvidesStatements;
    }


    /**
     * Get the number of {@code uses} statements added to this instance.
     *
     * @return  The number of {@code uses} statements.
     */
    public int getNumUsesStatements()
    {
        return fNumUsesStatements;
    }


    /**
     * Get the number of {@code opens} statements added to this instance.
     *
     * @return  The number of {@code opens} statements.
     */
    public int getNumOpensStatements()
    {
        return fNumOpensStatements;
    }


    /**
     * Add metrics of a {@code requires} statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull ModuleRequiresStmt pStatement)
    {
        requireNonNull(pStatement);
        fNumRequiresStatements++;
    }


    /**
     * Add metrics of an {@code exports} statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull ModuleExportsStmt pStatement)
    {
        requireNonNull(pStatement);
        fNumExportsStatements++;
    }


    /**
     * Add metrics of a {@code provides} statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull ModuleProvidesStmt pStatement)
    {
        requireNonNull(pStatement);
        fNumProvidesStatements++;
    }


    /**
     * Add metrics of a {@code uses} statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull ModuleUsesStmt pStatement)
    {
        requireNonNull(pStatement);
        fNumUsesStatements++;
    }


    /**
     * Add metrics of an {@code opens} statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull ModuleOpensStmt pStatement)
    {
        requireNonNull(pStatement);
        fNumOpensStatements++;
    }
}
