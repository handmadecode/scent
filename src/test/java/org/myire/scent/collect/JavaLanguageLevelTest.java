/*
 * Copyright 2021 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


/**
 * Unit tests for {@code org.myire.scent.collect.JavaLanguageLevel}.
 */
public class JavaLanguageLevelTest
{
    /**
     * {@code getDefault()} should return a non-null value.
     */
    @Test
    public void getDefaultReturnsNonNullValue()
    {
        assertNotNull(JavaLanguageLevel.getDefault());
    }


    /**
     * {@code forLevel()} should return the instance that has the specified numeric value.
     */
    @Test
    public void forLevelReturnsTheExpectedInstance()
    {
        for (JavaLanguageLevel aLevel : JavaLanguageLevel.values())
            assertSame(aLevel, JavaLanguageLevel.forNumericValue(aLevel.getNumericValue()));
    }


    /**
     * {@code forLevel()} should return null for a negative argument.
     */
    @Test
    public void forLevelReturnsNullForNegativeArgument()
    {
        assertNull(JavaLanguageLevel.forNumericValue(-1));
    }


    /**
     * {@code forLevel()} should return null for an argument greater than the highest supported
     * language level.
     */
    @Test
    public void forLevelReturnsNullForTooLargeArgument()
    {
        assertNull(JavaLanguageLevel.forNumericValue(4711));
    }
}
