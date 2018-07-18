/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.PackageMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstPackage;


/**
 * Unit tests related to parsing and collecting metrics for packages.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class PackageCollectTest
{
    /**
     * A parsed package declaration should be collected into a {@code PackageMetrics} with the
     * expected name.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void packageDeclarationIsCollected() throws ParseException
    {
        // Given
        String aPackage = "com.acme.util";
        String aSrc = "package " + aPackage + ";";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(aPackage, getFirstPackage(aMetrics).getName());
    }


    /**
     * Parsing multiple compilation units with the same package name should result in only one
     * {@code PackageMetrics} instance.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void packagesWithSameNameAreCollectedInTheSameMetrics() throws ParseException
    {
        // Given
        String aPackage = "com.acme.util";
        String aSrc = "package " + aPackage + ";";
        JavaMetricsCollector aParser = new JavaMetricsCollector();

        // When
        aParser.collect("1", aSrc);
        aParser.collect("2", aSrc);
        aParser.collect("3", aSrc);

        // Then
        Iterator<PackageMetrics> aIterator = aParser.getCollectedMetrics().iterator();
        assertEquals(aPackage, aIterator.next().getName());
        assertFalse(aIterator.hasNext());
    }


    /**
     * Parsing multiple compilation units with different package names should result in different
     * {@code PackageMetrics} instances.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void packagesWithDifferentNamesAreCollectedInDifferentMetrics() throws ParseException
    {
        // Given
        String aPackage1 = "com.acme.lang";
        String aPackage2 = "com.acme.io";
        String aPackage3 = "com.acme.util";
        JavaMetricsCollector aParser = new JavaMetricsCollector();

        // When
        aParser.collect("1", "package " + aPackage1 + ";");
        aParser.collect("2", "package " + aPackage2 + ";");
        aParser.collect("3", "package " + aPackage3 + ";");
        Iterable<PackageMetrics> aMetrics = aParser.getCollectedMetrics();

        // Then
        Iterator<PackageMetrics> aIterator = aMetrics.iterator();
        assertEquals(aPackage1, aIterator.next().getName());
        assertEquals(aPackage2, aIterator.next().getName());
        assertEquals(aPackage3, aIterator.next().getName());
        assertFalse(aIterator.hasNext());
    }


    /**
     * Parsing the default package should result in a {@code PackageMetrics} instances with an empty
     * name.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void defaultPackageHasEmptyName() throws ParseException
    {
        // When
        Iterable<PackageMetrics> aMetrics = collect("class X {}");

        // Then
        assertTrue(getFirstPackage(aMetrics).getName().isEmpty());
    }
}
