/*
 * Copyright 2018-2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.github.javaparser.ParserConfiguration;


/**
 * The Java language levels supported by the metrics collection.
 */
public enum JavaLanguageLevel
{
    /**
     * Java 8.
     *<p>
     * New language features:
     *<ul>
     * <li>lambdas</li>
     * <li>type annotations</li>
     *</ul>
     */
    JAVA_8(8, ParserConfiguration.LanguageLevel.JAVA_8, ParserConfiguration.LanguageLevel.JAVA_8),

    /**
     * Java 9.
     *<p>
     * New language features:
     *<ul>
     * <li>modules</li>
     * <li>private interface methods</li>
     *</ul>
     */
    JAVA_9(9, ParserConfiguration.LanguageLevel.JAVA_9, ParserConfiguration.LanguageLevel.JAVA_9),

    /**
     * Java 10.
     *<p>
     * New language features:
     *<ul>
     * <li>local variable type inference (JEP 286)</li>
     *</ul>
     */
    JAVA_10(10, ParserConfiguration.LanguageLevel.JAVA_10, ParserConfiguration.LanguageLevel.JAVA_10_PREVIEW),

    /**
     * Java 11.
     *<p>
     * New language features:
     *<ul>
     * <li>local variable syntax for lambda parameters (JEP 323)</li>
     *</ul>
     */
    JAVA_11(11, ParserConfiguration.LanguageLevel.JAVA_11, ParserConfiguration.LanguageLevel.JAVA_11_PREVIEW),

    /**
     * Java 12.
     *<p>
     * New language feature previews:
     *<ul>
     * <li>switch expressions preview (JEP 325)</li>
     *</ul>
     */
    JAVA_12(12, ParserConfiguration.LanguageLevel.JAVA_12, ParserConfiguration.LanguageLevel.JAVA_12_PREVIEW),

    /**
     * Java 13.
     *<p>
     * New language feature previews:
     *<ul>
     * <li>text blocks preview (JEP 355)</li>
     * <li>switch expressions second preview (JEP 354))</li>
     *</ul>
     */
    JAVA_13(13, ParserConfiguration.LanguageLevel.JAVA_13, ParserConfiguration.LanguageLevel.JAVA_13_PREVIEW),

    /**
     * Java 14.
     *<p>
     * New language features:
     *<ul>
     * <li>switch expressions (JEP 361)</li>
     *</ul>
     *<p>
     * New language feature previews:
     *<ul>
     * <li>pattern matching for instanceof preview (JEP 305)</li>
     * <li>records preview (JEP 359)</li>
     * <li>text blocks second preview (JEP 368)</li>
     *</ul>
     */
    JAVA_14(14, ParserConfiguration.LanguageLevel.JAVA_14, ParserConfiguration.LanguageLevel.JAVA_14_PREVIEW),

    /**
     * Java 15.
     *<p>
     * New language features:
     *<ul>
     * <li>text blocks (JEP 378)</li>
     *</ul>
     *<p>
     * New language feature previews:
     *<ul>
     * <li>sealed classes preview (JEP 360)</li>
     * <li>pattern matching for instanceof second preview (JEP 375)</li>
     * <li>records second preview (JEP 384)</li>
     *</ul>
     */
    JAVA_15(15, ParserConfiguration.LanguageLevel.JAVA_15, ParserConfiguration.LanguageLevel.JAVA_15_PREVIEW),

    /**
     * Java 16.
     *<ul>
     * <li>pattern matching for instanceof (JEP 394)</li>
     * <li>records (JEP 395)</li>
     * <li>sealed classes second preview (JEP 397)</li>
     *</ul>
     */
    JAVA_16(16, ParserConfiguration.LanguageLevel.JAVA_16, ParserConfiguration.LanguageLevel.JAVA_16_PREVIEW),

    /**
     * Java 17.
     *<ul>
     * <li>Sealed classes (JEP 409)</li>
     * <li>pattern matching for switch preview (JEP 406)</li>
     *</ul>
     */
    JAVA_17(17, ParserConfiguration.LanguageLevel.JAVA_17, ParserConfiguration.LanguageLevel.JAVA_17_PREVIEW),
    ;

    // Array where element n holds the instance with numeric value n.
    static private final JavaLanguageLevel[] cNumericValuesToInstance;

    static
    {
        JavaLanguageLevel[] aInstances = JavaLanguageLevel.values();
        JavaLanguageLevel aHighestLevel = aInstances[aInstances.length-1];
        cNumericValuesToInstance = new JavaLanguageLevel[aHighestLevel.getNumericValue() + 1];
        for (JavaLanguageLevel aInstance : aInstances)
            cNumericValuesToInstance[aInstance.fNumericValue] = aInstance;
    }


    private final int fNumericValue;
    private final ParserConfiguration.LanguageLevel fParserLanguageLevel;
    private final ParserConfiguration.LanguageLevel fParserPreviewLanguageLevel;


    JavaLanguageLevel(
        int pNumericValue,
        @Nonnull ParserConfiguration.LanguageLevel pParserLanguageLevel,
        @Nonnull ParserConfiguration.LanguageLevel pParserLanguagePreviewLevel)
    {
        fNumericValue = pNumericValue;
        fParserLanguageLevel = requireNonNull(pParserLanguageLevel);
        fParserPreviewLanguageLevel = requireNonNull(pParserLanguagePreviewLevel);
    }


    /**
     * Get the numeric value of this language level.
     *
     * @return  The language level's numeric value.
     */
    public int getNumericValue()
    {
        return fNumericValue;
    }


    /**
     * Get the {@code ParserConfiguration.LanguageLevel} instance that corresponds to this language
     * level.
     *
     * @return  The corresponding {@code ParserConfiguration.LanguageLevel}, never null.
     */
    @Nonnull
    public ParserConfiguration.LanguageLevel getParserLanguageLevel()
    {
        return fParserLanguageLevel;
    }


    /**
     * Get the {@code ParserConfiguration.LanguageLevel} instance that corresponds to this language
     * level with previews enabled.
     *
     * @return  The corresponding preview {@code ParserConfiguration.LanguageLevel}, never null.
     */
    @Nonnull
    public ParserConfiguration.LanguageLevel getParserPreviewLanguageLevel()
    {
        return fParserPreviewLanguageLevel;
    }


    /**
     * Get the default language level for Java metrics collecting.
     *
     * @return  The default language level, never null.
     */
    @Nonnull
    static public JavaLanguageLevel getDefault()
    {
        return JAVA_17;
    }


    /**
     * Get the instance that has a specific numeric value.
     *
     * @param pNumericValue    The numeric value to get the instance for.
     *
     * @return  The instance with the specified numeric value, or null of there is no instance with
     *          that numeric value.
     */
    @CheckForNull
    static public JavaLanguageLevel forNumericValue(int pNumericValue)
    {
        if (pNumericValue < 0 || pNumericValue >= cNumericValuesToInstance.length)
            return null;

        return cNumericValuesToInstance[pNumericValue];
    }
}
