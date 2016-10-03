/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.mock;

import org.myire.scent.metrics.PackageMetrics;

import static org.myire.scent.collect.CollectTestUtil.collect;


/**
 * Unit tests for {@code org.myire.scent.collect.JavaMetricsCollector}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class JavaMetricsCollectorTest
{
    /**
     * The method {@code collect} should throw a {@code NullPointerException} when passed a null
     * compilation unit name argument.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test(expected=NullPointerException.class)
    public void collectThrowsForNullCompilationUnitName() throws ParseException
    {
        new JavaMetricsCollector().collect(null, mock(InputStream.class), StandardCharsets.ISO_8859_1);
    }


    /**
     * The method {@code collect} should throw a {@code NullPointerException} when passed a null
     * input stream argument.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test(expected=NullPointerException.class)
    public void collectThrowsForNullInputStream() throws ParseException
    {
        new JavaMetricsCollector().collect("test", null, StandardCharsets.ISO_8859_1);
    }


    /**
     * The method {@code collect} should throw a {@code NullPointerException}  when passed a null
     * charset argument.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test(expected=NullPointerException.class)
    public void collectThrowsForNullCharset() throws ParseException
    {
        new JavaMetricsCollector().collect("test", mock(InputStream.class), null);
    }


    /**
     * The method {@code collect(String)} should throw a {@code ParseException} when passed a
     * syntactically invalid argument.
     *
     * @throws ParseException   always.
     */
    @Test(expected=ParseException.class)
    public void collectThrowsForInvalidSourceCode() throws ParseException
    {
        collect("int x = 2;");
    }


    /**
     * The method {@code getCollectedMetrics} should return an empty {@code Iterable} if no source
     * has been parsed.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void collectMetricsReturnsEmptyMetricsWhenParseHasNotBeenCalled() throws ParseException
    {
        // When
        Iterable<PackageMetrics> aMetrics = new JavaMetricsCollector().getCollectedMetrics();

        // Then
        assertFalse(aMetrics.iterator().hasNext());
    }
}
