/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;


/**
 * Unit tests for {@code StatementMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class StatementMetricsTest
{
    /**
     * A newly created {@code StatementMetrics} should have zero statements.
     */
    @Test
    public void newInstanceHasZeroStatements()
    {
        // When
        StatementMetrics aMetrics = new StatementMetrics();

        // Then
        assertEquals(0, aMetrics.getNumStatements());
    }


    /**
     * Adding a null {@code StatementMetrics} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullStatementMetricsThrows()
    {
        // Given
        StatementMetrics aMetrics = null;

        // When
        new StatementMetrics().add(aMetrics);
    }


    /**
     * Adding a {@code StatementMetrics} should increment the count of statements with the values
     * from the added instance.
     */
    @Test
    public void addingStatementMetricsIncreasesCounts()
    {
        // Given
        StatementMetrics aMetrics = createStatementMetrics(2);
        StatementMetrics aMetricsToAdd = createStatementMetrics(17);

        // When
        aMetrics.add(aMetricsToAdd);

        // Then
        assertEquals(19, aMetrics.getNumStatements());
    }


    /**
     * Adding a {@code Statement} should increase the count of statements.
     */
    @Test
    public void addingStatementIncreasesCount()
    {
        // Given
        StatementMetrics aMetrics = new StatementMetrics();
        int aNumStatements = aMetrics.getNumStatements();

        // When
        aMetrics.add(new ExpressionStmt());

        // Then
        assertEquals(++aNumStatements, aMetrics.getNumStatements());

        // When
        aMetrics.add(new SwitchStmt());

        // Then
        assertEquals(++aNumStatements, aMetrics.getNumStatements());
    }


    /**
     * Create a new {@code StatementMetrics} with a specific number of statements.
     *
     * @param pNumStatements    The number of statements in the new instance.
     *
     * @return  A new {@code StatementMetrics} instance
     */
    static StatementMetrics createStatementMetrics(int pNumStatements)
    {
        StatementMetrics aMetrics = new StatementMetrics();
        for (int i=0; i<pNumStatements; i++)
            aMetrics.add(new ExpressionStmt());
        return aMetrics;
    }
}
