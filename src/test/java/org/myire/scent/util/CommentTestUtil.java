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

import org.myire.scent.metrics.CommentMetrics;


/**
 * Utility methods for unit tests related to comments.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public final class CommentTestUtil
{
    // Characters that count as comment length (a selection).
    static private final char[] COMMENT_CHARS = "abcdefghijklmnopqrtsuvwxuzåäöüg".toCharArray();


    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private CommentTestUtil()
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
        return createLineComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "");
    }


    /**
     * Create a {@code LineComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pLength       The comment's content length.
     *
     * @return  A new {@code LineComment}.
     */
    static public LineComment createLineComment(
        int pBeginLine,
        int pBeginColumn,
        int pEndLine,
        int pEndColumn,
        int pLength)
    {
        return createLineComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, createContent(pLength));
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
        return createBlockComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "");
    }


    /**
     * Create a {@code BlockComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pLength       The comment's content length.
     *
     * @return  A new {@code BlockComment}.
     */
    static public BlockComment createBlockComment(
        int pBeginLine,
        int pBeginColumn,
        int pEndLine,
        int pEndColumn,
        int pLength)
    {
        return createBlockComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, createContent(pLength));
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
        return createJavadocComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, "");
    }


    /**
     * Create a {@code JavadocComment}.
     *
     * @param pBeginLine    The comment's begin line.
     * @param pBeginColumn  The comment's begin column.
     * @param pEndLine      The comment's end line.
     * @param pEndColumn    The comment's end column.
     * @param pLength       The comment's content length.
     *
     * @return  A new {@code JavadocComment}.
     */
    static public JavadocComment createJavadocComment(
        int pBeginLine,
        int pBeginColumn,
        int pEndLine,
        int pEndColumn,
        int pLength)
    {
        return createJavadocComment(pBeginLine, pBeginColumn, pEndLine, pEndColumn, createContent(pLength));
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


    /**
     * Create a string with a certain length consisting only of characters that count as comment
     * length.
     *
     * @param pLength   The length of the string to create.
     *
     * @return  A new string.
     */
    static private String createContent(int pLength)
    {
        if (pLength <= COMMENT_CHARS.length)
            return new String(COMMENT_CHARS, 0, pLength);

        StringBuilder aBuilder = new StringBuilder(pLength);
        do
        {
            int aNumChars = Math.min(COMMENT_CHARS.length, pLength);
            aBuilder.append(COMMENT_CHARS, 0, aNumChars);
            pLength -= aNumChars;
        }
        while (pLength > 0);

        return aBuilder.toString();
    }


    /**
     * Builder for {@code CommentMetrics} instances to help create test scenarios.
     */
    static public class Builder
    {
        int fNumLineComments;
        int fLineCommentsLength;
        int fNumBlockComments;
        int fNumBlockCommentLines;
        int fBlockCommentsLength;
        int fNumJavaDocComments;
        int fNumJavaDocLines;
        int fJavaDocCommentsLength;

        public CommentMetrics build()
        {
            CommentMetrics aMetrics = new CommentMetrics();

            // Add n-1 line comments with 1 line.
            for (int i = 0; i < fNumLineComments - 1; i++)
                aMetrics.add(createLineComment(i+1, 0, i+1, 1));

            // Add one line comment with 1 line and the entire line comment length.
            aMetrics.add(
                createLineComment(
                    fNumLineComments,
                    0,
                    fNumLineComments,
                    fLineCommentsLength,
                    fLineCommentsLength));

            // Add n-1 block comments with 1 line.
            for (int i = 0; i < fNumBlockComments - 1; i++)
                aMetrics.add(createBlockComment(i+1, 0, i+1, 3));

            // Add one block comment with the remaining lines and the entire block comment length.
            int aNumLines = fNumBlockCommentLines - fNumBlockComments;
            aMetrics.add(createBlockComment(1, 0, 1 + aNumLines, 3, fBlockCommentsLength));

            // Add n-1 JavaDoc comments with 1 line.
            for (int i = 0; i < fNumJavaDocComments - 1; i++)
                aMetrics.add(createJavadocComment(i+1, 0, i+1, 4));

            // Add one JavaDoc comment with the remaining lines and the entire JavaDoc comment
            // length.
            aNumLines = fNumJavaDocLines - fNumJavaDocComments;
            aMetrics.add(createJavadocComment(1, 0, 1 + aNumLines, 4, fJavaDocCommentsLength));

            return aMetrics;
        }

        public Builder withLineComments(int pNumComments, int pLength)
        {
            fNumLineComments = pNumComments;
            fLineCommentsLength = pLength;
            return this;
        }

        public Builder withBlockComments(int pNumComments, int pNumLines, int pLength)
        {
            fNumBlockComments = pNumComments;
            fNumBlockCommentLines = pNumLines;
            fBlockCommentsLength = pLength;
            return this;
        }

        public Builder withJavaDocComments(int pNumComments, int pNumLines, int pLength)
        {
            fNumJavaDocComments = pNumComments;
            fNumJavaDocLines = pNumLines;
            fJavaDocCommentsLength = pLength;
            return this;
        }
    }
}
