/*
 * Copyright 2016, 2018-2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.myire.scent.metrics.JavaMetrics;


/**
 * Unit tests for {@code org.myire.scent.collect.JavaMetricsCollector}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class JavaMetricsCollectorTest
{
    /**
     * {@code getLanguageLevel()} should return the value passed to the constructor.
     */
    @Test
    public void getLanguageLevelReturnsValuePassedToConstructor()
    {
        // Given
        JavaLanguageLevel aLevel = JavaLanguageLevel.JAVA_14;

        // When
        JavaMetricsCollector aCollector = new JavaMetricsCollector(aLevel);

        // Then
        assertEquals(aLevel, aCollector.getLanguageLevel());
    }


    /**
     * {@code hasPreviewsEnabled()} should return the value passed to the constructor.
     */
    @Test
    public void hasPreviewsEnabledReturnsValuePassedToConstructor()
    {
        // When
        JavaMetricsCollector aCollector = new JavaMetricsCollector(JavaLanguageLevel.JAVA_12, true);

        // Then
        assertTrue(aCollector.hasPreviewsEnabled());
    }


    /**
     * {@code hasPreviewsEnabled()} should return the default value if the no-args constructor is
     * used.
     */
    @Test
    public void hasPreviewsEnabledReturnsDefaultValue()
    {
        // When
        JavaMetricsCollector aCollector = new JavaMetricsCollector();

        // Then
        assertFalse(aCollector.hasPreviewsEnabled());
    }


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
     * The method {@code getCollectedMetrics} should return an empty {@code Iterable} if no source
     * has been parsed.
     */
    @Test
    public void collectMetricsReturnsEmptyMetricsWhenParseHasNotBeenCalled()
    {
        // When
        JavaMetrics aMetrics = new JavaMetricsCollector().getCollectedMetrics();

        // Then
        assertFalse(aMetrics.getPackages().iterator().hasNext());
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
        new JavaMetricsCollector().collect("src", "int x = 2;");
    }


    /**
     * A {@code JavaMetricsCollector} should produce the expected result for all test fixtures and
     * all language levels.
     */
    @Test
    public void collectProducesTheExpectedResultForAllLanguageLevels()
    {
        for (JavaLanguageLevel aLanguageLevel : JavaLanguageLevel.values())
        {
            for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
                aFixture.assertCollectWithLanguageLevel(aLanguageLevel, false);

            for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
                aFixture.assertCollectWithLanguageLevel(aLanguageLevel, true);
        }
    }
}
