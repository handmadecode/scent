/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.myire.scent.metrics.JavaMetrics;


/**
 * A {@code MetricsReportWriter} writes the source code metrics in a {@code JavaMetrics} instance to
 * a destination of some sort and on a format of some kind.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public interface MetricsReportWriter
{
    /**
     * Write a report for the source code metrics in a {@code JavaMetrics} instance.
     *
     * @param pMetrics  The metrics to get the report data from.
     * @param pMetaData The report's meta data.
     *
     * @throws IOException if writing the report fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    void writeReport(@Nonnull JavaMetrics pMetrics, @Nonnull MetricsReportMetaData pMetaData) throws IOException;
}
