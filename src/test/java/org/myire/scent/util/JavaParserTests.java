/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.util;

import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import static com.github.javaparser.Range.range;


/**
 * Utility methods related to tests involving JavaParser.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public final class JavaParserTests
{
    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private JavaParserTests()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Create a {@code LineComment}.
     *
     * @return  A new {@code LineComment}.
     */
    static public LineComment createLineComment()
    {
        return createLineComment(1, 1, 1, 2);
    }


    /**
     * Create a {@code LineComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     *
     * @return  A new {@code LineComment}.
     */
    static public LineComment createLineComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn)
    {
        return createLineComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "//");
    }


    /**
     * Create a {@code LineComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pContent      The comment's textual content.
     *
     * @return  A new {@code LineComment}.
     */
    static public LineComment createLineComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn,
            String pContent)
    {
        LineComment aComment = new LineComment(pContent);
        aComment.setRange(range(pBeginLine, pBeginColumn, pEndLine, pEndColumn));
        return aComment;
    }


    /**
     * Create a {@code BlockComment}.
     *
     * @param pNumLines The number of lines the block comment should span.
     *
     * @return  A new {@code BlockComment}.
     */
    static public BlockComment createBlockComment(int pNumLines)
    {
        return createBlockComment(1, 1, pNumLines, 4);
    }


    /**
     * Create a {@code BlockComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     *
     * @return  A new {@code BlockComment}.
     */
    static public BlockComment createBlockComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn)
    {
        return createBlockComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "/**/");
    }


    /**
     * Create a {@code BlockComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pContent      The comment's textual content.
     *
     * @return  A new {@code BlockComment}.
     */
    static public BlockComment createBlockComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn,
            String pContent)
    {
        BlockComment aComment = new BlockComment(pContent);
        aComment.setRange(range(pBeginLine, pBeginColumn, pEndLine, pEndColumn));
        return aComment;
    }


    /**
     * Create a {@code JavadocComment}.
     *
     * @param pNumLines The number of lines the JavaDoc comment should span.
     *
     * @return  A new {@code JavadocComment}.
     */
    static public JavadocComment createJavadocComment(int pNumLines)
    {
        return createJavadocComment(1, 1, pNumLines, 6);
    }


    /**
     * Create a {@code JavadocComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     *
     * @return  A new {@code JavadocComment}.
     */
    static public JavadocComment createJavadocComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn)
    {
        return createJavadocComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "/** */");
    }


    /**
     * Create a {@code JavadocComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pContent      The comment's textual content.
     *
     * @return  A new {@code JavadocComment}.
     */
    static public JavadocComment createJavadocComment(
            int pBeginLine,
            int pBeginColumn,
            int pEndLine,
            int pEndColumn,
            String pContent)
    {
        JavadocComment aComment = new JavadocComment(pContent);
        aComment.setRange(range(pBeginLine, pBeginColumn, pEndLine, pEndColumn));
        return aComment;
    }
}
