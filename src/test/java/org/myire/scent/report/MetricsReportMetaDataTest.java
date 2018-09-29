/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@code MetricsReportMetaData}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class MetricsReportMetaDataTest
{
    /**
     * The {@code getTimestamp()} method should return null if the timestamp passed to the
     * constructor was null.
     */
    @Test
    public void getTimestampReturnsNullWhenConstructorParameterIsNull()
    {
        // When
        LocalDateTime aTimestamp = new MetricsReportMetaData(null, "").getTimestamp();

        // Then
        assertNull(aTimestamp);
    }


    /**
     * The {@code getTimestamp()} method should return the timestamp passed to the constructor.
     */
    @Test
    public void getTimestampReturnsTimestampPassedToConstructor()
    {
        // Given
        LocalDateTime aTimestamp1 = LocalDateTime.of(2016, 9, 27, 11, 33, 1);

        // When
        LocalDateTime aTimestamp2 = new MetricsReportMetaData(aTimestamp1, null).getTimestamp();

        // Then
        assertEquals(aTimestamp1, aTimestamp2);
    }


    /**
     * The {@code getVersionString()} method should return null if the version string passed to the
     * constructor was null.
     */
    @Test
    public void getVersionStringReturnsNullWhenConstructorParameterIsNull()
    {
        // When
        String aVersion = new MetricsReportMetaData(LocalDateTime.now(), null).getVersionString();

        // Then
        assertNull(aVersion);
    }


    /**
     * The {@code getVersionString()} method should return the version string passed to the
     * constructor.
     */
    @Test
    public void getVersionStringReturnsVersionStringPassedToConstructor()
    {
        // Given
        String aVersion1 = "3.14-dev.1-20180316T173045";

        // When
        String aVersion2 = new MetricsReportMetaData(null, aVersion1).getVersionString();

        // Then
        assertEquals(aVersion1, aVersion2);
    }
}
