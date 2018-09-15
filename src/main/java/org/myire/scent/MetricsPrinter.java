/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.PrintStream;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.myire.scent.metrics.AggregatedMetrics;
import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.StatementMetrics;
import org.myire.scent.metrics.TypeMetrics;


/**
 * A {@code MetricsPrinter} prints source code metrics a {@code PrintStream}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
class MetricsPrinter
{
    private final PrintStream fPrintStream;
    private int fIndentation;


    /**
     * Create a new {@code MetricsPrinter}.
     *
     * @param pPrintStream  The stream to print to.
     *
     * @throws NullPointerException if {@code pPrintStream} is null.
     */
    MetricsPrinter(@Nonnull PrintStream pPrintStream)
    {
        fPrintStream = requireNonNull(pPrintStream);
    }


    /**
     * Print a {@code JavaMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull JavaMetrics pMetrics)
    {
        for (PackageMetrics aMetrics : pMetrics.getPackages())
            print(aMetrics);
    }


    /**
     * Print the values of a {@code PackageMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull PackageMetrics pMetrics)
    {
        // Print the package name as header.
        printIndentedLine("package ", pMetrics.getName());

        fIndentation++;

        // Print the aggregation for the entire package.
        print(AggregatedMetrics.ofChildren(pMetrics));

        fIndentation++;

        // Print each compilation unit.
        for (CompilationUnitMetrics aCompilationUnit : pMetrics.getCompilationUnits())
            print(aCompilationUnit);

        fIndentation--;

        fIndentation--;
    }


    /**
     * Print the values of a {@code CompilationUnitMetrics} to the stream specified in the
     * constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull CompilationUnitMetrics pMetrics)
    {
        // Print the compilation unit name as header.
        printIndentedLine(pMetrics.getName());

        fIndentation++;

        // Print the aggregation for the entire compilation unit.
        print(AggregatedMetrics.ofChildren(pMetrics));

        fIndentation++;

        // Print each type.
        for (TypeMetrics aType : pMetrics.getTypes())
            print(aType);

        fIndentation--;

        fIndentation--;
    }


    /**
     * Print the values of a {@code TypeMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull TypeMetrics pMetrics)
    {
        // Print the type's kind and name as header.
        printIndentedLine(pMetrics.getKind(), pMetrics.getName());

        fIndentation++;

        // Print the aggregation for the entire type.
        print(AggregatedMetrics.ofChildren(pMetrics));

        fIndentation++;

        // Print each field.
        for (FieldMetrics aField : pMetrics.getFields())
            print(aField);

        // Print each method.
        for (MethodMetrics aMethod : pMetrics.getMethods())
            print(aMethod);

        // Print each inner type.
        for (TypeMetrics aInnerType : pMetrics.getInnerTypes())
            print(aInnerType);

        fIndentation--;

        fIndentation--;
    }


    /**
     * Print the values of a {@code MethodMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull MethodMetrics pMetrics)
    {
        // Print the method's kind and name as header.
        printIndentedLine(pMetrics.getKind(), pMetrics.getName());

        fIndentation++;

        // Print the method's statements and comments.
        print(pMetrics.getStatements());
        print(pMetrics.getComments());

        // Print each local type.
        printIndentedLine(pMetrics.getNumLocalTypes(), " local types");
        for (TypeMetrics aLocalType : pMetrics.getLocalTypes())
            print(aLocalType);

        fIndentation--;
    }


    /**
     * Print the values of a {@code FieldMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull FieldMetrics pMetrics)
    {
        // Print the field's kind and name as header.
        printIndentedLine(pMetrics.getKind(), pMetrics.getName());

        // Print the field's statements and comments.
        fIndentation++;
        print(pMetrics.getStatements());
        print(pMetrics.getComments());
        fIndentation--;
    }


    /**
     * Print the values of a {@code StatementMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull StatementMetrics pMetrics)
    {
        printIndentedLine(pMetrics.getNumStatements(), " statements");
    }


    /**
     * Print the values of a {@code CommentMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull CommentMetrics pMetrics)
    {
        printIndentedLine(
            pMetrics.getNumLineComments(),
            pMetrics.getLineCommentsLength(),
            " line comments/length");
        printIndentedLine(
            pMetrics.getNumBlockComments(),
            pMetrics.getNumBlockCommentLines(),
            pMetrics.getBlockCommentsLength(),
            " block comments/lines/length");
        printIndentedLine(
            pMetrics.getNumJavaDocComments(),
            pMetrics.getNumJavaDocLines(),
            pMetrics.getJavaDocCommentsLength(),
            " JavaDocs/lines/length");
    }


    /**
     * Print the values of an {@code AggregatedMetrics} to the stream specified in the constructor.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    void print(@Nonnull AggregatedMetrics pMetrics)
    {
        printIndentedLine(pMetrics.getNumPackages(), " packages");
        printIndentedLine(pMetrics.getNumCompilationUnits(), " compilation units");
        printIndentedLine(pMetrics.getNumTypes(), " types");
        printIndentedLine(pMetrics.getNumMethods(), " methods");
        printIndentedLine(pMetrics.getNumFields(), " fields");
        printIndentedLine(pMetrics.getNumStatements(), " statements");
        printIndentedLine(
            pMetrics.getNumLineComments(),
            pMetrics.getLineCommentsLength(),
            " line comments/length");
        printIndentedLine(
            pMetrics.getNumBlockComments(),
            pMetrics.getNumBlockCommentLines(),
            pMetrics.getBlockCommentsLength(),
            " block comments/lines/length");
        printIndentedLine(
            pMetrics.getNumJavaDocComments(),
            pMetrics.getNumJavaDocLines(),
            pMetrics.getJavaDocCommentsLength(),
            " JavaDocs/lines/length");
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of an integer value immediately followed by a string. The line is only printed if
     * the value is positive.
     *
     * @param pValue    The integer value to print.
     * @param pText     The string to print.
     */
    private void printIndentedLine(int pValue, @Nullable String pText)
    {
        if (pValue > 0)
        {
            printIndentation();
            fPrintStream.print(pValue);
            fPrintStream.println(pText);
        }
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of two integer values separated by a slash ('/'), immediately followed by a string.
     * The line is only printed if the first value is positive.
     *
     * @param pValue1   The first integer value to print.
     * @param pValue2   The second integer value to print.
     * @param pText     The string to print.
     */
    private void printIndentedLine(int pValue1, int pValue2, @Nullable  String pText)
    {
        if (pValue1 > 0)
        {
            printIndentation();
            fPrintStream.print(pValue1);
            fPrintStream.print('/');
            fPrintStream.print(pValue2);
            fPrintStream.println(pText);
        }
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of three integer values separated by a slash ('/'), immediately followed by a
     * string. The line is only printed if the first value is positive.
     *
     * @param pValue1   The first integer value to print.
     * @param pValue2   The second integer value to print.
     * @param pValue3   The third integer value to print.
     * @param pText     The string to print.
     */
    private void printIndentedLine(int pValue1, int pValue2, int pValue3, @Nullable  String pText)
    {
        if (pValue1 > 0)
        {
            printIndentation();
            fPrintStream.print(pValue1);
            fPrintStream.print('/');
            fPrintStream.print(pValue2);
            fPrintStream.print('/');
            fPrintStream.print(pValue3);
            fPrintStream.println(pText);
        }
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of a single string.
     *
     * @param pString   The string to print.
     */
    private void printIndentedLine(@Nullable String pString)
    {
        printIndentation();
        fPrintStream.println(pString);
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of two strings with no separator between them.
     *
     * @param pString1  The first string to print.
     * @param pString2  The second string to print.
     */
    private void printIndentedLine(@Nullable String pString1, @Nullable String pString2)
    {
        printIndentation();
        fPrintStream.print(pString1);
        fPrintStream.println(pString2);
    }


    /**
     * Print a line at the current indentation to the stream specified in the constructor. The line
     * consists of an enum constant followed by a string with a space separating them.
     *
     * @param pEnum     The enum constant to print.
     * @param pString   The string to print.
     *
     * @throws NullPointerException if {@code pEnum} is null.
     */
    private void printIndentedLine(@Nonnull Enum<?> pEnum, @Nullable String pString)
    {
        printIndentation();
        fPrintStream.print(pEnum.name().toLowerCase());
        fPrintStream.print(' ');
        fPrintStream.println(pString);
    }


    /**
     * Print the current indentation to the stream specified in the constructor. Each indentation
     * level is represented by two spaces.
     */
    private void printIndentation()
    {
        for (int i=0; i<fIndentation; i++)
            fPrintStream.print("  ");
    }
}
