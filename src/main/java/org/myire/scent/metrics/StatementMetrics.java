/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.stmt.Statement;


/**
 * Metrics for source code statements.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class StatementMetrics
{
    private int fNumStatements;


    /**
     * Get the total number of statements collected by this instance.
     *
     * @return  The number of statements.
     */
    public int getNumStatements()
    {
        return fNumStatements;
    }


    /**
     * Add the values of another {@code StatementMetrics} to this instance.
     *
     * @param pValues   The values to add.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    public void add(@Nonnull StatementMetrics pValues)
    {
        fNumStatements += pValues.fNumStatements;
    }


    /**
     * Add metrics of a statement.
     *
     * @param pStatement    The statement.
     *
     * @throws NullPointerException if {@code pStatement} is null.
     */
    public void add(@Nonnull Statement pStatement)
    {
        requireNonNull(pStatement);
        fNumStatements++;
    }
}
