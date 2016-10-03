/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Base class for metrics associated with a source code element such as a compilation unit, a field
 * or a method. A source code element has a name and holds metrics for any comments associated with
 * the element.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class CodeElementMetrics
{
    private final String fName;
    private final CommentMetrics fCommentMetrics = new CommentMetrics();


    /**
     * Create a new {@code CodeElementMetrics}.
     *
     * @param pName The name of the code element.
     *
     * @throws NullPointerException if {@code pName} is null.
     */
    protected CodeElementMetrics(@Nonnull String pName)
    {
        fName = requireNonNull(pName);
    }


    /**
     * Get the name of this code element.
     *
     * @return  The name, never null.
     */
    @Nonnull
    public String getName()
    {
        return fName;
    }


    /**
     * Get the comment metrics associated with this code element.
     *
     * @return  The comment metrics, never null.
     */
    @Nonnull
    public CommentMetrics getComments()
    {
        return fCommentMetrics;
    }
}
