/*
 * Copyright 2016, 2018-2021 Peter Franzen. All rights reserved.
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

import org.myire.scent.metrics.JavaMetrics;


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
     * A {@code JavaMetricsCollector} for language level 8 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava8LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_8, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_8, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 9 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava9LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_9, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_9, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 10 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava10LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_10, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_10, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 11 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava11LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_11, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_11, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 12 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava12LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_12, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_12, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 13 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava13LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_13, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_13, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 14 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava14LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_14, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_14, true);
    }


    /**
     * A {@code JavaMetricsCollector} for language level 15 should produce the expected result for
     * all test fixtures.
     */
    @Test
    public void collectProducesTheExpectedResultForJava15LanguageLevel()
    {
        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_15, false);

        for (LanguageLevelFixture aFixture : LanguageLevelFixture.FIXTURES)
            aFixture.assertCollectWithLanguageLevel(JavaMetricsCollector.LanguageLevel.JAVA_15, true);
    }
}
