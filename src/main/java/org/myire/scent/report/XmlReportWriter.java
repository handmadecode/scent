/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.myire.scent.metrics.AggregatedMetrics;
import org.myire.scent.metrics.CodeElementMetrics;
import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.ModularCompilationUnitMetrics;
import org.myire.scent.metrics.ModuleDeclarationMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;


/**
 * A writer of metrics reports on XML format to an {@code OutputStream}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class XmlReportWriter extends OutputStreamReportWriter
{
    static private final char[] PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".toCharArray();


    /**
     * Create a new {@code XmlReportWriter} that uses UTF-8 as encoding.
     *
     * @param pOutputStream The stream to write the report to.
     *
     * @throws NullPointerException if {@code pOutputStream} is null.
     */
    public XmlReportWriter(@Nonnull OutputStream pOutputStream)
    {
        super(pOutputStream, StandardCharsets.UTF_8);
    }


    @Override
    protected void writeReportContents(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull MetricsReportMetaData pMetaData)
    {
        // XML prolog on the first line.
        writeProlog();

        // Root element.
        writeElement(
            "scent-report",
            this::writeReportAttributes,
            pMetaData,
            this::writeReportBody,
            pMetrics
        );
    }


    /**
     * Write a timestamp and a version string as attributes of the current element. The timestamp
     * will be split into two attributes, one with the date part and one with the time part.
     *
     * @param pMetaData The report meta data with the timestamp and version string to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    private void writeReportAttributes(@Nonnull MetricsReportMetaData pMetaData)
    {
        LocalDateTime aTimestamp = pMetaData.getTimestamp();
        if (aTimestamp != null)
        {
            writeAttribute("date", DateTimeFormatter.ISO_DATE.format(aTimestamp));
            writeAttribute("time", DateTimeFormatter.ISO_LOCAL_TIME.format(aTimestamp));
        }

        String aVersion = pMetaData.getVersionString();
        if (aVersion != null)
            writeAttribute("version", aVersion);
    }


    /**
     * Write the elements that make up the body of the entire report as child elements to the
     * current element.
     *
     * @param pMetrics  The metrics to populate the child elements with.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeReportBody(@Nonnull JavaMetrics pMetrics)
    {
        // Summary element for the entire report.
        writeSummaryElement(AggregatedMetrics.of(pMetrics));

        // A sequence with one modular compilation unit per child element.
        writeSequence(
            "modular-compilation-units",
            pMetrics.getNumModularCompilationUnits(),
            "modular-compilation-unit",
            this::writeCodeElementNameAttribute,
            this::writeModularCompilationUnitBody,
            pMetrics.getModularCompilationUnits()
        );

        // A sequence with one package per child element.
        writeSequence(
            "packages",
            pMetrics.getNumPackages(),
            "package",
            this::writeCodeElementNameAttribute,
            this::writePackageBody,
            pMetrics.getPackages()
        );
    }


    /**
     * Write a summary element with attributes from an {@code AggregatedMetrics} as child element of
     * the current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeSummaryElement(@Nonnull AggregatedMetrics pMetrics)
    {
        writeElement("summary", this::writeSummaryAttributes, pMetrics);
    }


    /**
     * Write the non-zero values from an {@code AggregatedMetrics} as summary attributes to the
     * current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeSummaryAttributes(@Nonnull AggregatedMetrics pMetrics)
    {
        writeAttributeIfPositive("modular-compilation-units", pMetrics.getNumModularCompilationUnits());
        writeAttributeIfPositive("packages", pMetrics.getNumPackages());
        writeAttributeIfPositive("compilation-units", pMetrics.getNumCompilationUnits());
        writeAttributeIfPositive("types", pMetrics.getNumTypes());
        writeAttributeIfPositive("methods", pMetrics.getNumMethods());
        writeAttributeIfPositive("fields", pMetrics.getNumFields());
        writeAttributeIfPositive("statements", pMetrics.getNumStatements());
        writeAttributeIfPositive("line-comments", pMetrics.getNumLineComments());
        writeAttributeIfPositive("line-comments-length", pMetrics.getLineCommentsLength());
        writeAttributeIfPositive("block-comments", pMetrics.getNumBlockComments());
        writeAttributeIfPositive("block-comments-lines", pMetrics.getNumBlockCommentLines());
        writeAttributeIfPositive("block-comments-length", pMetrics.getBlockCommentsLength());
        writeAttributeIfPositive("javadocs", pMetrics.getNumJavaDocComments());
        writeAttributeIfPositive("javadoc-lines", pMetrics.getNumJavaDocLines());
        writeAttributeIfPositive("javadocs-length", pMetrics.getJavaDocCommentsLength());
    }


    /**
     * Write a comments element with attributes from a {@code CommentMetrics} as child element of
     * the current element. No element will be written of the {@code CommentMetrics} instance is
     * empty.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeCommentsElement(@Nonnull CommentMetrics pMetrics)
    {
        if (!pMetrics.isEmpty())
            writeElement("comments", this::writeCommentsAttributes, pMetrics);
    }


    /**
     * Write the non-zero values from a {@code CommentMetrics} as comment attributes to the current
     * element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeCommentsAttributes(@Nonnull CommentMetrics pMetrics)
    {
        writeAttributeIfPositive("line-comments", pMetrics.getNumLineComments());
        writeAttributeIfPositive("line-comments-length", pMetrics.getLineCommentsLength());
        writeAttributeIfPositive("block-comments", pMetrics.getNumBlockComments());
        writeAttributeIfPositive("block-comments-lines", pMetrics.getNumBlockCommentLines());
        writeAttributeIfPositive("block-comments-length", pMetrics.getBlockCommentsLength());
        writeAttributeIfPositive("javadocs", pMetrics.getNumJavaDocComments());
        writeAttributeIfPositive("javadoc-lines", pMetrics.getNumJavaDocLines());
        writeAttributeIfPositive("javadocs-length", pMetrics.getJavaDocCommentsLength());
    }


    /**
     * Write the comment metrics and module declaration metrics of a
     * {@code ModularCompilationUnitMetrics} as child elements of the current element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeModularCompilationUnitBody(@Nonnull ModularCompilationUnitMetrics pMetrics)
    {
        writeCommentsElement(pMetrics.getComments());
        writeElement(
            "module",
            this::writeModuleAttributes,
            pMetrics.getModule(),
            // The element body is the module declaration's comment metrics.
            this::writeCommentsElement,
            pMetrics.getModule().getComments()
        );
    }


    /**
     * Write the name, openness, and statement counts of a {@code ModuleDeclarationMetrics} as
     * attributes of the current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeModuleAttributes(@Nonnull ModuleDeclarationMetrics pMetrics)
    {
        writeNameAttribute(pMetrics.getName());
        writeAttribute("open", pMetrics.isOpen() ? "true" : "false");
        writeAttributeIfPositive("requires", pMetrics.getNumRequiresStatements());
        writeAttributeIfPositive("exports", pMetrics.getNumExportsStatements());
        writeAttributeIfPositive("provides", pMetrics.getNumProvidesStatements());
        writeAttributeIfPositive("uses", pMetrics.getNumUsesStatements());
        writeAttributeIfPositive("opens", pMetrics.getNumOpensStatements());
    }


    /**
     * Write the aggregated summary of a {@code PackageMetrics} and a sequence of compilation unit
     * metrics as child elements of the current element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writePackageBody(@Nonnull PackageMetrics pMetrics)
    {
        writeSummaryElement(AggregatedMetrics.ofChildren(pMetrics));
        writeSequence(
            "compilation-units",
            pMetrics.getNumCompilationUnits(),
            "compilation-unit",
            this::writeCodeElementNameAttribute,
            this::writeCompilationUnitBody,
            pMetrics.getCompilationUnits()
        );
    }


    /**
     * Write the aggregated summary of a {@code CompilationUnitMetrics}, the comment metrics, and a
     * sequence of type metrics as child elements of the current element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeCompilationUnitBody(@Nonnull CompilationUnitMetrics pMetrics)
    {
        writeSummaryElement(AggregatedMetrics.ofChildren(pMetrics));
        writeCommentsElement(pMetrics.getComments());
        writeSequence(
            "types",
            pMetrics.getNumTypes(),
            "type",
            this::writeTypeAttributes,
            this::writeTypeBody,
            pMetrics.getTypes()
        );
    }


    /**
     * Write the name and kind of a {@code TypeMetrics} as attributes of the current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeTypeAttributes(@Nonnull TypeMetrics pMetrics)
    {
        writeNameAttribute(pMetrics.getName());
        writeKindAttribute(pMetrics.getKind());
    }


    /**
     * Write the aggregated summary of a {@code TypeMetrics}, the comment metrics, and sequences
     * with the metrics of the fields, methods, and inner types as child elements of the
     * current element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeTypeBody(@Nonnull TypeMetrics pMetrics)
    {
        writeSummaryElement(AggregatedMetrics.ofChildren(pMetrics));
        writeCommentsElement(pMetrics.getComments());
        writeSequence(
            "fields",
            pMetrics.getNumFields(),
            "field",
            this::writeFieldAttributes,
            // The field body contains only the comment metrics.
            this::writeCodeElementComments,
            pMetrics.getFields()
        );
        writeSequence(
            "methods",
            pMetrics.getNumMethods(),
            "method",
            this::writeMethodAttributes,
            this::writeMethodBody,
            pMetrics.getMethods()
        );
        writeSequence(
            "inner-types",
            pMetrics.getNumInnerTypes(),
            "inner-type",
            this::writeTypeAttributes,
            this::writeTypeBody,
            pMetrics.getInnerTypes()
        );
    }


    /**
     * Write the name, kind, and number of statements of a {@code FieldMetrics} as attributes to the
     * current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeFieldAttributes(@Nonnull FieldMetrics pMetrics)
    {
        writeNameAttribute(pMetrics.getName());
        writeKindAttribute(pMetrics.getKind());
        writeStatementsAttribute(pMetrics.getStatements().getNumStatements());
    }


    /**
     * Write the name, kind, and number of statements of a {@code MethodMetrics} as attributes to
     * the current element.
     *
     * @param pMetrics  The instance to get the attribute values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeMethodAttributes(@Nonnull MethodMetrics pMetrics)
    {
        writeNameAttribute(pMetrics.getName());
        writeKindAttribute(pMetrics.getKind());
        writeStatementsAttribute(pMetrics.getStatements().getNumStatements());
    }


    /**
     * Write the aggregated summary of a {@code MethodMetrics}, the comment metrics, and a sequence
     * of local types metrics as child elements of the current element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeMethodBody(@Nonnull MethodMetrics pMetrics)
    {
        writeSummaryElement(AggregatedMetrics.ofChildren(pMetrics));
        writeCommentsElement(pMetrics.getComments());
        writeSequence(
            "local-types",
            pMetrics.getNumLocalTypes(),
            "local-type",
            this::writeTypeAttributes,
            this::writeTypeBody,
            pMetrics.getLocalTypes()
        );
    }


    /**
     * Write the name of a {@code CodeElementMetrics} as attribute of the current element.
     *
     * @param pMetrics  The instance to get the attribute value from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeCodeElementNameAttribute(@Nonnull CodeElementMetrics pMetrics)
    {
        writeNameAttribute(pMetrics.getName());
    }


    /**
     * Write the comment metrics of a {@code CodeElementMetrics} as child element of the current
     * element.
     *
     * @param pMetrics  The instance to get the child element values from.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    private void writeCodeElementComments(@Nonnull CodeElementMetrics pMetrics)
    {
        writeCommentsElement(pMetrics.getComments());
    }


    /**
     * Write the &quot;name&quot; attribute of the current element.
     *
     * @param pValue    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeNameAttribute(@Nonnull String pValue)
    {
        writeAttribute("name", pValue);
    }


    /**
     * Write the &quot;kind&quot; attribute of the current element.
     *
     * @param pValue    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeKindAttribute(@Nonnull Enum<?> pValue)
    {
        writeAttribute("kind", pValue.name().toLowerCase());
    }


    /**
     * Write the &quot;count&quot; attribute of the current element.
     *
     * @param pValue    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeCountAttribute(@Nonnull String pValue)
    {
        writeAttribute("count", pValue);
    }


    /**
     * Write the &quot;statements&quot; attribute of the current element. The attribute will only be
     * written if the statement count is positive.
     *
     * @param pNumStatements    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeStatementsAttribute(int pNumStatements)
    {
        writeAttributeIfPositive("statements", pNumStatements);
    }


    /**
     * Write the XML prolog to the underlying stream.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    private void writeProlog()
    {
        write(PROLOG);
        writeLineBreak();
    }


    /**
     * Write a sequence of elements as child elements to a sequence root element, which in its turn
     * is a child element to the current element.
     *<p>
     * Example:
     *<pre>
     * &lt;sequence-root&gt;
     *   &lt;sequence-child/&gt;
     *   &lt;sequence-child/&gt;
     * &lt;/sequence-root&gt;
     *</pre>
     *
     * If the number of child elements is zero, nothing will be written (not even the root element).
     *
     * @param pRootElementName      The name of the sequence root element.
     * @param pChildCount           The number of child elements in the sequence.
     * @param pChildElementName     The name of the sequence child elements.
     * @param pChildAttributeWriter A consumer that will write the attributes of each child element.
     * @param pChildBodyWriter      A consumer that will write the body of each child element.
     * @param pChildren             The instances with the values for the child elements.
     *
     * @param <T>   The type containing child element values.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the reference parameters is null.
     */
    private <T> void writeSequence(
        @Nonnull String pRootElementName,
        int pChildCount,
        @Nonnull String pChildElementName,
        @Nonnull Consumer<T> pChildAttributeWriter,
        @Nonnull Consumer<T> pChildBodyWriter,
        @Nonnull Iterable<T> pChildren)
    {
        if (pChildCount > 0)
        {
            // Write the opening root element tag with the child count as its only attribute.
            writeElementStart(
                pRootElementName,
                this::writeCountAttribute,
                String.valueOf(pChildCount));

            // Write each child element as an indented element, delegating the actual writing to the
            // consumers passed as parameters.
            increaseIndentationLevel();
            pChildren.forEach(
                c -> writeElement(pChildElementName, pChildAttributeWriter, c, pChildBodyWriter, c));
            decreaseIndentationLevel();

            // Write the closing root element tag.
            writeElementEnd(pRootElementName);
        }
    }


    /**
     * Write an element as child element of the current element.
     *
     * @param pElementName      The name of the element.
     * @param pAttributeWriter  A consumer that writes the element's attributes.
     * @param pAttributes       An instance with the attribute values.
     * @param pBodyWriter       A consumer that writes the element's body.
     * @param pBodyData         An instance with the body values.
     *
     * @param <A>   The type containing attribute values.
     * @param <B>   The type containing body values.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private <A, B> void writeElement(
        @Nonnull String pElementName,
        @Nonnull Consumer<A> pAttributeWriter,
        @Nonnull A pAttributes,
        @Nonnull Consumer<B> pBodyWriter,
        @Nonnull B pBodyData)
    {
        // Write the opening element tag with attributes.
        writeElementStart(pElementName, pAttributeWriter, pAttributes);

        // Write the body at the next indentation level, delegating the actual writing to the
        // consumer passed as parameter.
        increaseIndentationLevel();
        pBodyWriter.accept(pBodyData);
        decreaseIndentationLevel();

        // Write the closing element tag.
        writeElementEnd(pElementName);
    }


    /**
     * Write an element containing only attributes as child element of the current element.
     *
     * @param pElementName      The name of the element.
     * @param pAttributeWriter  A consumer that writes the element's attributes.
     * @param pAttributes       An instance with the attribute values.
     *
     * @param <A>   The type containing attribute values.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the reference parameters is null.
     */
    private <A> void writeElement(
        @Nonnull String pElementName,
        @Nonnull Consumer<A> pAttributeWriter,
        @Nonnull A pAttributes)
    {
        writeIndentation();
        write('<');
        write(pElementName);
        pAttributeWriter.accept(pAttributes);
        write('/');
        write('>');
        writeLineBreak();
    }


    /**
     * Write the opening tag of an element on a separate line.
     *
     * @param pElementName      The name of the element.
     * @param pAttributeWriter  A consumer that writes the element's attributes.
     * @param pAttributes       An instance with the attribute values.
     *
     * @param <A>   The type containing attribute values.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the reference parameters is null.
     */
    private <A> void writeElementStart(
        @Nonnull String pElementName,
        @Nonnull Consumer<A> pAttributeWriter,
        @Nonnull A pAttributes)
    {
        writeIndentation();
        write('<');
        write(pElementName);
        pAttributeWriter.accept(pAttributes);
        write('>');
        writeLineBreak();
    }


    /**
     * Write the closing tag of an element on a separate line.
     *
     * @param pElementName  The name of the element.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pName} is null.
     */
    private void writeElementEnd(@Nonnull String pElementName)
    {
        writeIndentation();
        write('<');
        write('/');
        write(pElementName);
        write('>');
        writeLineBreak();
    }


    /**
     * Write an attribute of the current element.
     *
     * @param pName     The attribute's name.
     * @param pValue    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void writeAttribute(@Nonnull String pName, @Nonnull String pValue)
    {
        write(' ');
        write(pName);
        write('=');
        write('"');
        writeEscaped(pValue);
        write('"');
    }


    /**
     * Write an integer attribute of the current element. The attribute will only be written if the
     * value is greater than zero.
     *
     * @param pName     The attribute's name.
     * @param pValue    The attribute's value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pName} is null.
     */
    private void writeAttributeIfPositive(@Nonnull String pName, int pValue)
    {
        if (pValue > 0)
        {
            write(' ');
            write(pName);
            write('=');
            write('"');
            write(String.valueOf(pValue));
            write('"');
        }
    }


    /**
     * Write an attribute value, escaping any special XML characters such as '&lt;'.
     *
     * @param pValue    The attribute value.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pValue} is null.
     */
    private void writeEscaped(@Nonnull String pValue)
    {
        int aLength = pValue.length();
        for (int i=0; i<aLength; i++)
        {
            char aChar = pValue.charAt(i);
            if (aChar == '<')
                write("&lt;");
            else if (aChar == '"')
                write("&quot;");
            else if (aChar == '&')
                write("&amp;");
            else
                write(aChar);
        }
    }
}
