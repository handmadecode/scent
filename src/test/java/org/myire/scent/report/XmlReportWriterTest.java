/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@code MetricsXmlReportWriter}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class XmlReportWriterTest extends OutputStreamReportWriterTestBase
{
    static private final XPathFactory cXPathFactory = XPathFactory.newInstance();
    static private final DocumentBuilderFactory cDocumentBuilderFactory =
        DocumentBuilderFactory.newInstance();


    @Override
    protected OutputStreamReportWriter createReportWriter(OutputStream pOutputStream)
    {
        return new XmlReportWriter(pOutputStream);
    }


    /**
     * Calling the constructor with a null argument should throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullArgument()
    {
        // When
        new XmlReportWriter(null);
    }


    /**
     * The report should contain the date and time from the report meta data passed to
     * {@code writeReportContents}.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsTimestamp() throws SAXException
    {
        // Given
        LocalDateTime aTimeStamp = LocalDateTime.of(2017, 3, 11, 23, 59, 1);

        // When
        writeReport(aTimeStamp, null);

        // Then
        Document aDocument = parseReport();
        assertEquals("2017-03-11", getXPath(aDocument, "/scent-report/@date"));
        assertEquals("23:59:01", getXPath(aDocument, "/scent-report/@time"));
    }


    /**
     * The report should contain the version string from the report meta data passed to
     * {@code writeReportContents}.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsVersion() throws SAXException
    {
        // Given
        String aVersion = "3.99";

        // When
        writeReport(null, aVersion);

        // Then
        Document aDocument = parseReport();
        assertEquals(aVersion, getXPath(aDocument, "/scent-report/@version"));
    }


    /**
     * The report should contain the modular compilation unit metrics and module declaration
     * metrics collected from the Java sources.
     *
     * @throws SAXException     if the report is malformed.
     */
    @Test
    public void reportContainsCollectedModules() throws SAXException
    {
        // When
        collectAndWriteReport(
            "module x { exports org.myire.scent; }",
            "open module y { requires java.sql; requires java.xml;}");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@modular-compilation-units"));
        assertEquals("2", getXPath(aDocument, "/scent-report/modular-compilation-units/@count"));

        Object aModule =
            getXPathNode(aDocument, "/scent-report/modular-compilation-units/modular-compilation-unit[1]/module");
        assertEquals("x", getXPath(aModule, "@name"));
        assertEquals("false", getXPath(aModule, "@open"));
        assertEquals("1", getXPath(aModule, "@exports"));

        aModule =
            getXPathNode(aDocument, "/scent-report/modular-compilation-units/modular-compilation-unit[2]/module");
        assertEquals("y", getXPath(aModule, "@name"));
        assertEquals("true", getXPath(aModule, "@open"));
        assertEquals("2", getXPath(aModule, "@requires"));
    }


    /**
     * The report should contain the comments from the collected modular compilation unit metrics
     * and module declaration metrics.
     *
     * @throws SAXException     if the report is malformed.
     */
    @Test
    public void reportContainsModuleComments() throws SAXException
    {
        // When
        collectAndWriteReport(
            "/* Compilation unit comment */ \n\n // Module comment\n module x { exports org.myire.scent; }"
        );

        // Then
        Document aDocument = parseReport();
        Object aComments =
            getXPathNode(aDocument, "/scent-report/modular-compilation-units/modular-compilation-unit[1]/comments");
        assertEquals("1", getXPath(aComments, "@block-comments"));
        assertEquals("1", getXPath(aComments, "@block-comments-lines"));
        assertEquals("24", getXPath(aComments, "@block-comments-length"));

        aComments =
            getXPathNode(aDocument, "/scent-report/modular-compilation-units/modular-compilation-unit[1]/module/comments");
        assertEquals("1", getXPath(aComments, "@line-comments"));
        assertEquals("14", getXPath(aComments, "@line-comments-length"));
    }


    /**
     * The report should contain the package metrics collected from the Java sources.
     *
     * @throws SAXException     if the report is malformed.
     */
    @Test
    public void reportContainsCollectedPackages() throws SAXException
    {
        // When
        collectAndWriteReport("package x;", "package y;");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@packages"));
        assertEquals("2", getXPath(aDocument, "/scent-report/packages/@count"));
        assertEquals("x", getXPath(aDocument, "/scent-report/packages/package[1]/@name"));
        assertEquals("y", getXPath(aDocument, "/scent-report/packages/package[2]/@name"));
    }


    /**
     * The report should contain the compilation unit metrics collected from the Java sources.
     *
     * @throws SAXException     if the report is malformed.
     */
    @Test
    public void reportContainsCollectedCompilationUnits() throws SAXException
    {
        // When
        collectAndWriteReport("package x;", "class X{}", "interface Z {}");

        // Then
        Document aDocument = parseReport();
        assertEquals("3", getXPath(aDocument, "/scent-report/summary/@compilation-units"));

        Object aPackageNode = getXPathNode(aDocument, "/scent-report/packages/package[1]");
        assertEquals("1", getXPath(aPackageNode, "compilation-units/@count"));
        assertEquals("Test0.java", getXPath(aPackageNode, "compilation-units/compilation-unit[1]/@name"));

        aPackageNode = getXPathNode(aDocument, "/scent-report/packages/package[2]");
        assertEquals("2", getXPath(aPackageNode, "compilation-units/@count"));
        assertEquals("Test1.java", getXPath(aPackageNode, "compilation-units/compilation-unit[1]/@name"));
        assertEquals("Test2.java", getXPath(aPackageNode, "compilation-units/compilation-unit[2]/@name"));
    }


    /**
     * The report should contain the type metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedTypes() throws SAXException
    {
        // When
        collectAndWriteReport("class Y{}", "interface Z{}", "enum W{}", "@interface Q{}");

        // Then
        Document aDocument = parseReport();
        assertEquals("4", getXPath(aDocument, "/scent-report/summary/@types"));

        Object aPackageNode = getXPathNode(aDocument, "/scent-report/packages/package[1]");
        Object aTypeNode = getXPathNode(aPackageNode, "compilation-units/compilation-unit[1]/types/type[1]");
        assertEquals("Y", getXPath(aTypeNode, "@name"));
        assertEquals("class", getXPath(aTypeNode, "@kind"));

        aTypeNode = getXPathNode(aPackageNode, "compilation-units/compilation-unit[2]/types/type[1]");
        assertEquals("Z", getXPath(aTypeNode, "@name"));
        assertEquals("interface", getXPath(aTypeNode, "@kind"));

        aTypeNode = getXPathNode(aPackageNode, "compilation-units/compilation-unit[3]/types/type[1]");
        assertEquals("W", getXPath(aTypeNode, "@name"));
        assertEquals("enum", getXPath(aTypeNode, "@kind"));

        aTypeNode = getXPathNode(aPackageNode, "compilation-units/compilation-unit[4]/types/type[1]");
        assertEquals("Q", getXPath(aTypeNode, "@name"));
        assertEquals("annotation", getXPath(aTypeNode, "@kind"));
    }


    /**
     * The report should contain the field metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedFields() throws SAXException
    {
        // When
        collectAndWriteReport("class Y{int fField; static long cField;}");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@fields"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("2", getXPath(aTypeNode, "fields/@count"));
        assertEquals("fField", getXPath(aTypeNode, "fields/field[1]/@name"));
        assertEquals("instance_field", getXPath(aTypeNode, "fields/field[1]/@kind"));
        assertEquals("cField", getXPath(aTypeNode, "fields/field[2]/@name"));
        assertEquals("static_field", getXPath(aTypeNode, "fields/field[2]/@kind"));
    }


    /**
     * The report should contain the method metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedMethods() throws SAXException
    {
        // When
        collectAndWriteReport("class Y{ native char n(); void v(){} static void sv(){} }");

        // Then
        Document aDocument = parseReport();
        assertEquals("3", getXPath(aDocument, "/scent-report/summary/@methods"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("3", getXPath(aTypeNode, "methods/@count"));
        assertEquals("char n()", getXPath(aTypeNode, "methods/method[1]/@name"));
        assertEquals("native_method", getXPath(aTypeNode, "methods/method[1]/@kind"));
        assertEquals("void v()", getXPath(aTypeNode, "methods/method[2]/@name"));
        assertEquals("instance_method", getXPath(aTypeNode, "methods/method[2]/@kind"));
        assertEquals("void sv()", getXPath(aTypeNode, "methods/method[3]/@name"));
        assertEquals("static_method", getXPath(aTypeNode, "methods/method[3]/@kind"));
    }


    /**
     * The report should contain the inner type metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedInnerTypes() throws SAXException
    {
        // When
        collectAndWriteReport("class Y{ interface InnerY{} }");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@types"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "inner-types/@count"));
        assertEquals("InnerY", getXPath(aTypeNode, "inner-types/inner-type[1]/@name"));
        assertEquals("interface", getXPath(aTypeNode, "inner-types/inner-type[1]/@kind"));
    }


    /**
     * The report should contain the local type metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedLocalTypes() throws SAXException
    {
        // When
        collectAndWriteReport("class X{ void m() { class LocalClass {} } }");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@types"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "methods/method[1]/local-types/@count"));
        assertEquals("LocalClass", getXPath(aTypeNode, "methods/method[1]/local-types/local-type[1]/@name"));
        assertEquals("class", getXPath(aTypeNode, "methods/method[1]/local-types/local-type[1]/@kind"));
    }


    /**
     * The report should contain the statement metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedStatements() throws SAXException
    {
        // When
        collectAndWriteReport("class Y{ int f=2; int m(){return 4711;} }");

        // Then
        Document aDocument = parseReport();
        assertEquals("2", getXPath(aDocument, "/scent-report/summary/@statements"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "fields/field[1]/@statements"));
        assertEquals("1", getXPath(aTypeNode, "methods/method[1]/@statements"));
    }


    /**
     * The report should contain the line comment metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedLineComments() throws SAXException
    {
        // When
        collectAndWriteReport(
            "// A class\n" +
            "class Y{\n" +
            "// One field\n" +
            "int f;\n" +
            "// A method\n void m(){} }");

        // Then
        Document aDocument = parseReport();
        assertEquals("3", getXPath(aDocument, "/scent-report/summary/@line-comments"));
        assertEquals("24", getXPath(aDocument, "/scent-report/summary/@line-comments-length"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "comments/@line-comments"));
        assertEquals("7", getXPath(aTypeNode, "comments/@line-comments-length"));
        assertEquals("1", getXPath(aTypeNode, "fields/field[1]/comments/@line-comments"));
        assertEquals("9", getXPath(aTypeNode, "fields/field[1]/comments/@line-comments-length"));
        assertEquals("1", getXPath(aTypeNode, "methods/method[1]/comments/@line-comments"));
        assertEquals("8", getXPath(aTypeNode, "methods/method[1]/comments/@line-comments-length"));
    }


    /**
     * The report should contain the block comment metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedBlockComments() throws SAXException
    {
        // When
        collectAndWriteReport(
            "/* This class */\n" +
            "class Y{\n" +
            "/* has a field*/\n" +
            "int f;\n" +
            "/* and a method\n" +
             "* of type void */\n" +
            "void m(){} }"
        );

        // Then
        Document aDocument = parseReport();
        assertEquals("3", getXPath(aDocument, "/scent-report/summary/@block-comments"));
        assertEquals("4", getXPath(aDocument, "/scent-report/summary/@block-comments-lines"));
        assertEquals("45", getXPath(aDocument, "/scent-report/summary/@block-comments-length"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "comments/@block-comments"));
        assertEquals("1", getXPath(aTypeNode, "comments/@block-comments-lines"));
        assertEquals("10", getXPath(aTypeNode, "comments/@block-comments-length"));
        assertEquals("1", getXPath(aTypeNode, "fields/field[1]/comments/@block-comments"));
        assertEquals("1", getXPath(aTypeNode, "fields/field[1]/comments/@block-comments-lines"));
        assertEquals("11", getXPath(aTypeNode, "fields/field[1]/comments/@block-comments-length"));
        assertEquals("1", getXPath(aTypeNode, "methods/method[1]/comments/@block-comments"));
        assertEquals("2", getXPath(aTypeNode, "methods/method[1]/comments/@block-comments-lines"));
        assertEquals("24", getXPath(aTypeNode, "methods/method[1]/comments/@block-comments-length"));
    }


    /**
     * The report should contain the JavaDoc comment metrics collected from the Java sources.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void reportContainsCollectedJavaDocComments() throws SAXException
    {
        // When
        collectAndWriteReport(
            "/** This class is documented. */\n" +
            "class Y{\n" +
            "/** This field has two\n" +
            "    JavaDoc lines. */\n" +
            "int f;\n" +
            "/** Inner type JavaDoc. */\n" +
            "interface Inner{} }"
        );

        // Then
        Document aDocument = parseReport();
        assertEquals("3", getXPath(aDocument, "/scent-report/summary/@javadocs"));
        assertEquals("4", getXPath(aDocument, "/scent-report/summary/@javadoc-lines"));
        assertEquals("76", getXPath(aDocument, "/scent-report/summary/@javadocs-length"));

        Object aTypeNode = getFirstTypeNode(aDocument);
        assertEquals("1", getXPath(aTypeNode, "comments/@javadocs"));
        assertEquals("1", getXPath(aTypeNode, "comments/@javadoc-lines"));
        assertEquals("25", getXPath(aTypeNode, "comments/@javadocs-length"));
        assertEquals("1", getXPath(aTypeNode, "fields/field[1]/comments/@javadocs"));
        assertEquals("2", getXPath(aTypeNode, "fields/field[1]/comments/@javadoc-lines"));
        assertEquals("32", getXPath(aTypeNode, "fields/field[1]/comments/@javadocs-length"));
        assertEquals("1", getXPath(aTypeNode, "inner-types/inner-type[1]/comments/@javadocs"));
        assertEquals("1", getXPath(aTypeNode, "inner-types/inner-type[1]/comments/@javadoc-lines"));
        assertEquals("19", getXPath(aTypeNode, "inner-types/inner-type[1]/comments/@javadocs-length"));
    }


    /**
     * A version string with XML special chars such as '&lt;' and '&quot;' should be escaped.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void versionWithSpecialCharsIsEscaped() throws SAXException
    {
        // Given
        String aVersion = "version <>&'\"";

        // When
        writeReport(null, aVersion);

        // Then
        Document aDocument = parseReport();
        assertEquals(aVersion, getXPath(aDocument, "/scent-report/@version"));
    }


    /**
     * A code element name with XML special chars such as '&lt;' should be escaped.
     *
     * @throws SAXException if the report is malformed.
     */
    @Test
    public void nameWithSpecialCharsIsEscaped() throws SAXException
    {
        // When
        collectAndWriteReport(
            "class C { void m(List<String> l) {}}"
        );

        // Then
        Object aTypeNode = getFirstTypeNode(parseReport());
        assertEquals("void m(List<String>)", getXPath(aTypeNode, "methods/method[1]/@name"));
    }


    /**
     * Parse the report written by {@link #writeReport(LocalDateTime, String)} or
     * {@link #collectAndWriteReport(String...)} into a {@code Document}.
     *
     * @return  The parsed {@code Document}.
     *
     * @throws SAXException if the report is malformed.
     */
    private Document parseReport() throws SAXException
    {
        try
        {
            return cDocumentBuilderFactory
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(getReportContents()));
        }
        catch (ParserConfigurationException | IOException e)
        {
            // ParserConfigurationException shouldn't be thrown since the default configuration is
            // used; IOException shouldn't be thrown since the input is a byte array.
            throw new RuntimeException(e);
        }
    }


    /**
     * Evaluate an XPath expression that returns the first type node from the first compilation unit
     * node from the first package node in a document.
     *
     * @param pDocument The document to evaluate the expression in.
     *
     * @return  The result of the evaluation.
     */
    static private Object getFirstTypeNode(Object pDocument)
    {
        return getXPathNode(
            pDocument,
            "/scent-report/packages/package[1]/compilation-units/compilation-unit[1]/types/type[1]");
    }


    /**
     * Evaluate an XPath expression that returns a string.
     *
     * @param pContext          The context to evaluate the expression in.
     * @param pXPathExpression  The expression to evaluate.
     *
     * @return  The result of the evaluation.
     */
    static private String getXPath(Object pContext, String pXPathExpression)
    {
        try
        {
            return cXPathFactory.newXPath().compile(pXPathExpression).evaluate(pContext);
        }
        catch (XPathExpressionException xpee)
        {
            // All XPath expressions are compile time constants in the tests; an exception here
            // means the test itself is malformed.
            throw new RuntimeException(xpee);
        }
    }


    /**
     * Evaluate an XPath expression that returns a node.
     *
     * @param pContext          The context to evaluate the expression in.
     * @param pXPathExpression  The expression to evaluate.
     *
     * @return  The result of the evaluation.
     */
    static private Object getXPathNode(Object pContext, String pXPathExpression)
    {
        try
        {
            return cXPathFactory.newXPath().compile(pXPathExpression).evaluate(pContext, XPathConstants.NODE);
        }
        catch (XPathExpressionException xpee)
        {
            // All XPath expressions are compile time constants in the tests; an exception here
            // means the test itself is malformed.
            throw new RuntimeException(xpee);
        }
    }
}
