/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Base class for metrics associated with a source code element that may contain statements.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class StatementElementMetrics extends CodeElementMetrics
{
    private final StatementMetrics fStatementMetrics = new StatementMetrics();


    /**
     * Create a new {@code StatementElementMetrics}.
     *
     * @param pName The name of the code element.
     *
     * @throws NullPointerException if {@code pName} is null.
     */
    protected StatementElementMetrics(@Nonnull String pName)
    {
        super(pName);
    }


    /**
     * Get the statement metrics associated with this code element.
     *
     * @return  The statement metrics, never null.
     */
    @Nonnull
    public StatementMetrics getStatements()
    {
        return fStatementMetrics;
    }
}
