/*
 * Copyright 2021 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.fail;

import  org.myire.scent.collect.JavaMetricsCollector.LanguageLevel;
import org.myire.scent.util.CollectTestUtil;


/**
 * A {@code LanguageLevelFixture} is a test fixture containing a Java code fragment that is
 * supported by some language levels and not by others. The method
 * {@link #assertCollectWithLanguageLevel(LanguageLevel, boolean)} asserts that a
 * {@code JavaMetricsCollector} for the specified language level either successfully collects
 * metrics or throws a {@code ParseException}, depending on whether the language level is in the
 * fixture's set of supported language levels or not.
 */
final class LanguageLevelFixture
{
    static final LanguageLevelFixture[] FIXTURES = {

        // Underscore variable name is not allowed in language levels >= 9
        new LanguageLevelFixture(
            "Underscore variable name",
            "class X {int _;}",
            EnumSet.of(LanguageLevel.JAVA_8),
            EnumSet.of(LanguageLevel.JAVA_8)
        ),

        // Private interface methods were introduced in Java 9.
        new LanguageLevelFixture(
            "Private interface method",
            "interface X { private void m() {} }",
            EnumSet.range(LanguageLevel.JAVA_9, LanguageLevel.JAVA_15),
            EnumSet.range(LanguageLevel.JAVA_9, LanguageLevel.JAVA_15)
        ),

        // Local variable type inference was introduced in Java 10, but Java 8 and 9 don't recognize
        // 'var' as a reserved type name and will thus parse 'var' as a normal type name.
        new LanguageLevelFixture(
            "Local variable type inference",
            "class X { void f() { var l = new java.util.ArrayList<String>(); } }",
            EnumSet.range(LanguageLevel.JAVA_8, LanguageLevel.JAVA_15),
            EnumSet.range(LanguageLevel.JAVA_8, LanguageLevel.JAVA_15)
        ),

        // Local variable syntax for lambda parameters were introduced in Java 11, but Java 8 and 9
        // don't recognize 'var' as a reserved type name and will thus parse 'var' as a normal type
        // name.
        new LanguageLevelFixture(
            "Local variable syntax for lambda parameters",
            "class X { void x(Map m) { m.forEach((var x, var y) -> System.out.println(x==y)); } }",
            allLevelsExcept(LanguageLevel.JAVA_10),
            allLevelsExcept(LanguageLevel.JAVA_10)
        ),

        // Switch expressions were introduced as a preview in Java 12 and as a standard feature in
        // Java 14.
        new LanguageLevelFixture(
            "Switch expression",
            "class X { int x(int p) { return switch (p) { case 1 -> 17; case 2 -> 4711; default -> 666; }; } }",
            EnumSet.of(LanguageLevel.JAVA_14, LanguageLevel.JAVA_15),
            EnumSet.range(LanguageLevel.JAVA_12, LanguageLevel.JAVA_15)
        ),

        // Returning from switch expressions with 'yield' were introduced as a preview in Java 13
        // and as a standard feature in Java 14.
        new LanguageLevelFixture(
            "Switch expression with yield",
            "class X { int y;int x(int p) { return switch (p) { case 1 -> {y++; yield 17;} default -> {y--; yield 666;} }; } }",
            EnumSet.of(LanguageLevel.JAVA_14, LanguageLevel.JAVA_15),
            EnumSet.range(LanguageLevel.JAVA_13, LanguageLevel.JAVA_15)
        ),

        // Text blocks were introduced as a preview in Java 13 and as a standard feature in Java 15.
        new LanguageLevelFixture(
            "Text blocks",
            CollectTestUtil.toSourceString(
                new String[]{
                    "class X {",
                    "  String aLines = \"\"\"",
                    "                   all",
                    "                   those",
                    "                   lines",
                    "                   \"\"\";",
                    "}"
                }
            ),
            EnumSet.of(LanguageLevel.JAVA_15),
            EnumSet.range(LanguageLevel.JAVA_13, LanguageLevel.JAVA_15)
        ),

        // Pattern matching for instanceof was introduced as a preview in Java 14 and as a standard
        // feature in Java 16.
        new LanguageLevelFixture(
            "Pattern matching for instanceof",
            "class X { int m(Object o) { if (o instanceof String s) return s.length(); else return 0; } }",
            EnumSet.noneOf(LanguageLevel.class),
            EnumSet.range(LanguageLevel.JAVA_14, LanguageLevel.JAVA_15)
        ),
    };


    private final String fName;
    private final String fSourceCode;
    private final Set<LanguageLevel> fLanguageLevels;
    private final Set<LanguageLevel> fPreviewLanguageLevels;


    /**
     * Create a new {@code LanguageLevelFixture}.
     *
     * @param pName                     The fixture's name, used in assertion messages.
     * @param pSourceCode               The source code to test metrics collection with.
     * @param pLanguageLevels           The language levels where the source code is supported as a
     *                                  standard feature.
     * @param pPreviewLanguageLevels    The language levels where the source code is supported as a
     *                                  preview feature.
     */
    LanguageLevelFixture(
        String pName,
        String pSourceCode,
        Set<JavaMetricsCollector.LanguageLevel> pLanguageLevels,
        Set<JavaMetricsCollector.LanguageLevel> pPreviewLanguageLevels)
    {
        fName = pName;
        fSourceCode = pSourceCode;
        fLanguageLevels = pLanguageLevels;
        fPreviewLanguageLevels = pPreviewLanguageLevels;
    }


    void assertCollectWithLanguageLevel(LanguageLevel pLanguageLevel, boolean pEnablePreviews)
    {
        boolean isSupported =
            pEnablePreviews ?
                fPreviewLanguageLevels.contains(pLanguageLevel) :
                fLanguageLevels.contains(pLanguageLevel);

        if (isSupported)
            assertCollectedMetrics(pLanguageLevel, pEnablePreviews);
        else
            assertParseException(pLanguageLevel, pEnablePreviews);
    }


    private void assertCollectedMetrics(LanguageLevel pLanguageLevel, boolean pEnablePreviews)
    {
        try
        {
            JavaMetricsCollector aCollector = new JavaMetricsCollector(pLanguageLevel, pEnablePreviews);
            aCollector.collect(fName, fSourceCode);
        }
        catch (ParseException pe)
        {
            String aMessage = "Language feature '" + fName + "' throws for " + pLanguageLevel;
            if (pEnablePreviews)
                aMessage += " with previews enabled";

            fail(aMessage + ": " + pe.getMessage());
        }
    }


    private void assertParseException(LanguageLevel pLanguageLevel, boolean pEnablePreviews)
    {
        try
        {
            JavaMetricsCollector aCollector = new JavaMetricsCollector(pLanguageLevel, pEnablePreviews);
            aCollector.collect(fName, fSourceCode);
            String aMessage = "Language feature '" + fName + "' does not throw for " + pLanguageLevel;
            if (pEnablePreviews)
                aMessage += " with previews enabled";

            fail(aMessage);
        }
        catch (ParseException pe)
        {
            // Expected exception.
        }
    }


    static private EnumSet<LanguageLevel> allLevelsExcept(LanguageLevel... pExclusions)
    {
        EnumSet<LanguageLevel> aSet = EnumSet.allOf(LanguageLevel.class);
        for (LanguageLevel aLevel : pExclusions)
            aSet.remove(aLevel);

        return aSet;
    }
}
