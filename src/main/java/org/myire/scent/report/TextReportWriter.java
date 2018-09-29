/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import org.myire.scent.metrics.AggregatedMetrics;
import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.ModularCompilationUnitMetrics;
import org.myire.scent.metrics.ModuleDeclarationMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.StatementMetrics;
import org.myire.scent.metrics.TypeMetrics;


/**
 * A writer of metrics reports on plain text format to an {@code OutputStream}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class TextReportWriter extends OutputStreamReportWriter
{
    /**
     * Create a new {@code TextReportWriter} that uses UTF-8 as encoding.
     *
     * @param pOutputStream The stream to write the report to.
     *
     * @throws NullPointerException if {@code pOutputStream} is null.
     */
    public TextReportWriter(@Nonnull OutputStream pOutputStream)
    {
        this(pOutputStream, StandardCharsets.UTF_8);
    }


    /**
     * Create a new {@code TextReportWriter}.
     *
     * @param pOutputStream The stream to write the report to.
     * @param pCharset      The character set to encode the report with.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public TextReportWriter(@Nonnull OutputStream pOutputStream, @Nonnull Charset pCharset)
    {
        super(pOutputStream, pCharset);
    }


    @Override
    protected void writeReportContents(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull MetricsReportMetaData pMetaData)
    {
        // Report header with meta data.
        writeIndentedLine(createReportHeader(pMetaData));

        // Aggregated summary.
        writeIndentedLine("Summary:");
        increaseIndentationLevel();
        write(AggregatedMetrics.of(pMetrics));
        decreaseIndentationLevel();

        // Each modular compilation unit and package as a separate section.
        writeIndentedLine("Details:");
        increaseIndentationLevel();
        pMetrics.getModularCompilationUnits().forEach(this::write);
        pMetrics.getPackages().forEach(this::write);
        decreaseIndentationLevel();
    }


    /**
     * Write the values of an {@code AggregatedMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull AggregatedMetrics pMetrics)
    {
        writeIndentedLine(pMetrics.getNumModularCompilationUnits(), " modules");
        writeIndentedLine(pMetrics.getNumPackages(), " packages");
        writeIndentedLine(pMetrics.getNumCompilationUnits(), " compilation units");
        writeIndentedLine(pMetrics.getNumTypes(), " types");
        writeIndentedLine(pMetrics.getNumMethods(), " methods");
        writeIndentedLine(pMetrics.getNumFields(), " fields");
        writeIndentedLine(pMetrics.getNumStatements(), " statements");
        writeLineComments(pMetrics.getNumLineComments(), pMetrics.getLineCommentsLength());
        writeBlockComments(
            pMetrics.getNumBlockComments(),
            pMetrics.getNumBlockCommentLines(),
            pMetrics.getBlockCommentsLength());
        writeJavaDocComments(
            pMetrics.getNumJavaDocComments(),
            pMetrics.getNumJavaDocLines(),
            pMetrics.getJavaDocCommentsLength());
    }


    /**
     * Write the values of a {@code ModularCompilationUnitMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull ModularCompilationUnitMetrics pMetrics)
    {
        // Write the modular compilation unit name as header for this section.
        writeIndentedLine(pMetrics.getName());

        // Write the comments and module declaration as an indented section.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        write(pMetrics.getModule());
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code ModuleDeclarationMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull ModuleDeclarationMetrics pMetrics)
    {
        // Write the module name as header for this section.
        if (pMetrics.isOpen())
            writeIndentedLine("open module ", pMetrics.getName());
        else
            writeIndentedLine("module ", pMetrics.getName());

        // Write the module declaration's comments and statement counts as an indented section.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        writeIndentedLine(pMetrics.getNumRequiresStatements(), " requires");
        writeIndentedLine(pMetrics.getNumExportsStatements(), " exports");
        writeIndentedLine(pMetrics.getNumProvidesStatements(), " provides");
        writeIndentedLine(pMetrics.getNumUsesStatements(), " uses");
        writeIndentedLine(pMetrics.getNumOpensStatements(), " opens");
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code PackageMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull PackageMetrics pMetrics)
    {
        // Write the package name as header for this section.
        writeIndentedLine("package ", pMetrics.getName());

        // Write the comments and each compilation unit as an indented section.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        pMetrics.getCompilationUnits().forEach(this::write);
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code CompilationUnitMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull CompilationUnitMetrics pMetrics)
    {
        // Write the compilation unit name as header for this section.
        writeIndentedLine(pMetrics.getName());

        // Write the comments and each type as an indented section.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        pMetrics.getTypes().forEach(this::write);
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code TypeMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull TypeMetrics pMetrics)
    {
        // Write the type's kind and name as header for this section.
        writeIndentedLine(pMetrics.getKind(), pMetrics.getName());

        // Write the comments and each field, method, and inner type as indented sections.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        pMetrics.getFields().forEach(this::write);
        pMetrics.getMethods().forEach(this::write);
        pMetrics.getInnerTypes().forEach(this::write);
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code MethodMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull MethodMetrics pMetrics)
    {
        // Write the method's kind and name as header for this section.
        writeIndentedLine(pMetrics.getKind(), pMetrics.getName());

        // Write the method's comments, statements, and local types as indented sections.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        write(pMetrics.getStatements());
        pMetrics.getLocalTypes().forEach(this::write);
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code FieldMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull FieldMetrics pMetrics)
    {
        // Write the field's kind and name as header for this section.
        writeIndentedLine(pMetrics.getKind(), pMetrics.getName());

        // Write the field's comments and statements as indented sections.
        increaseIndentationLevel();
        write(pMetrics.getComments());
        write(pMetrics.getStatements());
        decreaseIndentationLevel();
    }


    /**
     * Write the values of a {@code StatementMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull StatementMetrics pMetrics)
    {
        writeIndentedLine(pMetrics.getNumStatements(), " statements");
    }


    /**
     * Write the values of a {@code CommentMetrics} to the underlying stream.
     *
     * @param pMetrics  The metrics to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void write(@Nonnull CommentMetrics pMetrics)
    {
        writeLineComments(
            pMetrics.getNumLineComments(),
            pMetrics.getLineCommentsLength());
        writeBlockComments(
            pMetrics.getNumBlockComments(),
            pMetrics.getNumBlockCommentLines(),
            pMetrics.getBlockCommentsLength());
        writeJavaDocComments(
            pMetrics.getNumJavaDocComments(),
            pMetrics.getNumJavaDocLines(),
            pMetrics.getJavaDocCommentsLength());
    }


    /**
     * Write a line at the current indentation containing the number of line comments and their
     * total length to the underlying stream. The line will only be written if the number of line
     * comments is greater than zero.
     *
     * @param pNumLineComments      The number of line comments.
     * @param pLineCommentsLength   The total line comments length.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    private void writeLineComments(int pNumLineComments, int pLineCommentsLength)
    {
        if (pNumLineComments > 0)
        {
            writeIndentedLine(
                String.valueOf(pNumLineComments),
                " line comments, total length ",
                String.valueOf(pLineCommentsLength));
        }
    }


    /**
     * Write a line at the current indentation containing the number of block comments, the number
     * of block comment lines, and their total length to the underlying stream. The line will only
     * be written if the number of block comments is greater than zero.
     *
     * @param pNumBlockComments      The number of block comments.
     * @param pNumBlockCommentLines  The number of block comment lines.
     * @param pBlockCommentsLength   The total block comments length.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    private void writeBlockComments(
        int pNumBlockComments,
        int pNumBlockCommentLines,
        int pBlockCommentsLength)
    {
        if (pNumBlockComments > 0)
        {
            writeIndentedLine(
                String.valueOf(pNumBlockComments),
                " block comments on ",
                String.valueOf(pNumBlockCommentLines),
                " lines, total length ",
                String.valueOf(pBlockCommentsLength)
            );
        }
    }


    /**
     * Write a line at the current indentation containing the number of JavaDoc comments, the number
     * of JavaDoc comment lines, and their total length to the underlying stream. The line will only
     * be written if the number of JavaDoc comments is greater than zero.
     *
     * @param pNumJavaDocComments       The number of JavaDoc comments.
     * @param pNumJavaDocCommentLines   The number of JavaDoc comment lines.
     * @param pJavaDocCommentsLength    The total JavaDoc comments length.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    private void writeJavaDocComments(
        int pNumJavaDocComments,
        int pNumJavaDocCommentLines,
        int pJavaDocCommentsLength)
    {
        if (pNumJavaDocComments > 0)
        {
            writeIndentedLine(
                String.valueOf(pNumJavaDocComments),
                " JavaDoc comments on ",
                String.valueOf(pNumJavaDocCommentLines),
                " lines, total length ",
                String.valueOf(pJavaDocCommentsLength)
            );
        }
    }


    /**
     * Write a line at the current indentation to the underlying stream.
     *
     * @param pLine The string to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pLine} is null.
     */
    private void writeIndentedLine(@Nonnull String pLine)
    {
        writeIndentation();
        write(pLine);
        writeLineBreak();
    }


    /**
     * Write a line at the current indentation to the underlying stream. The line consists of a
     * sequence of strings with no separator between them.
     *
     * @param pStrings  The strings to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pStrings} or any of its elements is null.
     */
    private void writeIndentedLine(@Nonnull String... pStrings)
    {
        writeIndentation();
        for (String aString : pStrings)
            write(aString);
        writeLineBreak();
    }


    /**
     * Write a line at the current indentation to the underlying stream. The line consists of an
     * enum constant followed by a string with a space separating them.
     *
     * @param pEnum     The enum constant to write.
     * @param pString   The string to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeIndentedLine(@Nonnull Enum<?> pEnum, @Nonnull String pString)
    {
        writeIndentation();
        write(pEnum.name().toLowerCase());
        write(' ');
        write(pString);
        writeLineBreak();
    }


    /**
     * Write a line at the current indentation to the underlying stream. The line consists of an
     * integer value immediately followed by a string. The line is only written if the value is
     * greater than zero.
     *
     * @param pValue    The integer value to write.
     * @param pText     The string to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pText} is null.
     */
    private void writeIndentedLine(int pValue, @Nonnull String pText)
    {
        if (pValue > 0)
        {
            writeIndentation();
            write(String.valueOf(pValue));
            write(pText);
            writeLineBreak();
        }
    }


    /**
     * Create the report header.
     *
     * @param pMetaData The metadata with the timestamp and/or version string to include in the
     *                  header.
     *
     * @return  A string with the report header, never null.
     *
     *@throws NullPointerException if {@code pMetaData} is null.
     */
    @Nonnull
    static private String createReportHeader(@Nonnull MetricsReportMetaData pMetaData)
    {
        LocalDateTime aTimestamp = pMetaData.getTimestamp();
        String aVersion = pMetaData.getVersionString();
        if (aTimestamp == null && aVersion == null)
            // Empty meta data.
            return "Scent report";

        StringBuilder aHeader = new StringBuilder("Scent report created");

        if (aTimestamp != null)
        {
            aHeader.append(" on ")
                .append(DateTimeFormatter.ISO_DATE.format(aTimestamp))
                .append(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME.format(aTimestamp));
        }

        if (aVersion != null)
            aHeader.append(" with version ").append((aVersion));

        return aHeader.toString();
    }
}
