/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

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
    private int fNumBlockComments;
    private int fNumBlockCommentLines;
    private int fNumJavaDocComments;
    private int fNumJavaDocLines;


    /**
     * Is this instance empty?
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
     * Add the values of another {@code CommentMetrics} to this instance.
     *
     * @param pValues   The values to add.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    public void add(@Nonnull CommentMetrics pValues)
    {
        fNumLineComments += pValues.fNumLineComments;
        fNumBlockComments += pValues.fNumBlockComments;
        fNumBlockCommentLines += pValues.fNumBlockCommentLines;
        fNumJavaDocComments += pValues.fNumJavaDocComments;
        fNumJavaDocLines += pValues.fNumJavaDocLines;
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
        return pComment.getEndLine() - pComment.getBeginLine() + 1;
    }
}
