/*
 * Copyright 2016, 2018, 2020 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.Range;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;


/**
 * Metrics for source code comments, separated into block comments, line comments, and JavaDoc
 * comments.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads  without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class CommentMetrics
{
    private int fNumLineComments;
    private int fLineCommentsLength;
    private int fNumBlockComments;
    private int fNumBlockCommentLines;
    private int fBlockCommentsLength;
    private int fNumJavaDocComments;
    private int fNumJavaDocLines;
    private int fJavaDocCommentsLength;


    /**
     * Check if this instance is empty.
     *
     * @return  True if all comment counts are zero, false if at least one of them is non-zero.
     */
    public boolean isEmpty()
    {
        return fNumLineComments == 0 &&
               fNumBlockComments == 0 &&
               fNumJavaDocComments == 0;
    }


    /**
     * Get the number of line comments collected from the {@code LineComment} instances added to
     * this instance.
     *
     * @return  The number of line comments.
     */
    public int getNumLineComments()
    {
        return fNumLineComments;
    }


    /**
     * Get the total number of significant characters in the {@code LineComment} instances added to
     * this instance. Significant characters are the ones remaining when leading and trailing
     * whitespace has been trimmed away.
     *
     * @return  The content length of the line comments.
     */
    public int getLineCommentsLength()
    {
        return fLineCommentsLength;
    }


    /**
     * Get the number of block comments collected from the {@code BlockComment} instances added to
     * this instance.
     *
     * @return  The number of block comments.
     */
    public int getNumBlockComments()
    {
        return fNumBlockComments;
    }


    /**
     * Get the number of block comment lines collected from the {@code BlockComment} instances added
     * to this instance.
     *
     * @return  The number of block comment lines.
     */
    public int getNumBlockCommentLines()
    {
        return fNumBlockCommentLines;
    }


    /**
     * Get the total number of significant characters in the {@code BlockComment} instances added to
     * this instance. Significant characters are the ones remaining when leading and trailing
     * whitespace and asterisks have been trimmed away from each line in the comments.
     *
     * @return  The content length of the block comments.
     */
    public int getBlockCommentsLength()
    {
        return fBlockCommentsLength;
    }


    /**
     * Get the number of JavaDoc comments collected from the {@code JavadocComment} instances added
     * to this instance.
     *
     * @return  The number of JavaDoc comments.
     */
    public int getNumJavaDocComments()
    {
        return fNumJavaDocComments;
    }


    /**
     * Get the number of lines collected from the {@code JavadocComment} instances added to this
     * instance.
     *
     * @return  The number of JavaDoc comment lines.
     */
    public int getNumJavaDocLines()
    {
        return fNumJavaDocLines;
    }


    /**
     * Get the total number of significant characters in the {@code JavadocComment} instances added
     * to this instance. Significant characters are the ones remaining when leading and trailing
     * whitespace and asterisks have been trimmed away from each line in the comments.
     *
     * @return  The content length of the JavaDoc comments.
     */
    public int getJavaDocCommentsLength()
    {
        return fJavaDocCommentsLength;
    }


    /**
     * Add the values of another {@code CommentMetrics} to this instance.
     *
     * @param pValues   The values to add.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    public void add(@Nonnull CommentMetrics pValues)
    {
        fNumLineComments += pValues.fNumLineComments;
        fLineCommentsLength += pValues.fLineCommentsLength;
        fNumBlockComments += pValues.fNumBlockComments;
        fNumBlockCommentLines += pValues.fNumBlockCommentLines;
        fBlockCommentsLength += pValues.fBlockCommentsLength;
        fNumJavaDocComments += pValues.fNumJavaDocComments;
        fNumJavaDocLines += pValues.fNumJavaDocLines;
        fJavaDocCommentsLength += pValues.fJavaDocCommentsLength;
    }


    /**
     * Add metrics from a line comment.
     *
     * @param pComment  The line comment to add metrics from.
     *
     * @throws NullPointerException if {@code pComment} is null.
     */
    public void add(@Nonnull LineComment pComment)
    {
        fNumLineComments += getLineCount(pComment);
        fLineCommentsLength += getCommentLength(pComment);
    }


    /**
     * Add metrics from a block comment.
     *
     * @param pComment  The block comment to add metrics from.
     *
     * @throws NullPointerException if {@code pComment} is null.
     */
    public void add(@Nonnull BlockComment pComment)
    {
        fNumBlockComments++;
        fNumBlockCommentLines += getLineCount(pComment);
        fBlockCommentsLength += getCommentLength(pComment);
    }


    /**
     * Add metrics from a JavaDoc comment.
     *
     * @param pComment  The JavaDoc comment to add metrics from.
     *
     * @throws NullPointerException if {@code pComment} is null.
     */
    public void add(@Nonnull JavadocComment pComment)
    {
        fNumJavaDocComments++;
        fNumJavaDocLines += getLineCount(pComment);
        fJavaDocCommentsLength += getCommentLength(pComment);
    }


    /**
     * Get the number of lines that a comment spans.
     *
     * @param pComment  The comment to get the line count for.
     *
     * @return  The line count of the comment.
     *
     * @throws NullPointerException if {@code pComment} is null.
     */
    static private int getLineCount(@Nonnull Comment pComment)
    {
        Range aRange = pComment.getRange().orElse(null);
        return aRange != null ? aRange.getLineCount() : 0;
    }


    /**
     * Get the number of characters in a comment. Only the characters that remain after each line
     * in the comment's contents has been trimmed of leading and trailing white space and asterisks
     * are counted.
     *
     * @param pComment  The comment to get the number of characters for.
     *
     * @return  The number of characters in the comment.
     *
     * @throws NullPointerException if {@code pComment} is null.
     */
    static private int getCommentLength(@Nonnull Comment pComment)
    {
        int aLength = 0;
        String aContent = pComment.getContent();
        int aPos = 0, aNumChars = aContent.length();
        int aStart = 0, aEnd = 0;
        boolean aBeginningOfLine = true;
        while (aPos < aNumChars)
        {
            char aChar = aContent.charAt(aPos);
            if (isLineBreak(aChar))
            {
                // Found a line break, count the characters on the line that just ended and reset
                // the start and end of the character range on the next line.
                aLength += aEnd - aStart;
                aStart = aEnd = aPos;
                aBeginningOfLine = true;
            }
            else if (!isAsteriskOrWhitespace(aChar))
            {
                // The current character is a regular character (not a line break, nor whitespace or
                // asterisk).
                if (aBeginningOfLine)
                {
                    // We're at the beginning of the line, this character is the first that should
                    // be counted on the current line.
                    aBeginningOfLine = false;
                    aStart = aPos;
                }

                // The range of characters to count on the current line now ends after this
                // character.
                aEnd = aPos + 1;
            }

            // Move on to the next character. Note that nothing is done for whitespace or asterisks;
            // they don't count unless followed by a regular character on the same line, and if so
            // they will be included in the range of counted characters when that regular character
            // is detected.
            aPos++;
        }

        return aLength + (aEnd - aStart);
    }


    static private boolean isLineBreak(char pChar)
    {
        return pChar == '\r' || pChar == '\n';
    }


    static private boolean isAsteriskOrWhitespace(char pChar)
    {
        return pChar == '*' || Character.isWhitespace(pChar);
    }
}
