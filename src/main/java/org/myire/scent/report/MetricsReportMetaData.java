/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.time.LocalDateTime;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


/**
 * Meta data for a metrics report, such as the time when the report's metrics were collected.
 *<p>
 * Instances of this class are immutable.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@Immutable
public class MetricsReportMetaData
{
    private final LocalDateTime fTimestamp;
    private final String fVersionString;


    /**
     * Create a new {@code MetricsReportMetaData}.
     *
     * @param pTimestamp        The timestamp when the report's metrics were collected, or null if
     *                          the report shouldn't include a timestamp.
     * @param pVersionString    A string with the version of Scent used to collect the metrics, or
     *                          null if the report shouldn't include a version string.
     */
    public MetricsReportMetaData(@Nullable LocalDateTime pTimestamp, @Nullable String pVersionString)
    {
        fTimestamp = pTimestamp;
        fVersionString = pVersionString;
    }


    /**
     * Get the timestamp when the report's metrics were collected.
     *
     * @return  The timestamp, or null if the report shouldn't include a timestamp.
     */
    @CheckForNull
    public LocalDateTime getTimestamp()
    {
        return fTimestamp;
    }


    /**
     * Get the version of Scent used to collect the report's metrics.
     *
     * @return  A string with the Scent version, or null if the report shouldn't include a version
     *          string.
     */
    @CheckForNull
    public String getVersionString()
    {
        return fVersionString;
    }
}
