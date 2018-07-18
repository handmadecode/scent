/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;

import static org.myire.scent.util.CommentTestUtil.createBlockComment;
import static org.myire.scent.util.CommentTestUtil.createJavadocComment;
import static org.myire.scent.util.CommentTestUtil.createLineComment;


/**
 * Unit tests for {@code CommentMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class CommentMetricsTest
{
    /**
     * {@code isEmpty()} should return true for a newly created {@code CommentMetrics}.
     */
    @Test
    public void newInstanceIsEmpty()
    {
        // When
        CommentMetrics aMetrics = new CommentMetrics();

        // Then
        assertTrue(aMetrics.isEmpty());
    }


    /**
     * A newly created {@code CommentMetrics} should have zero comments.
     */
    @Test
    public void newInstanceHasZeroComments()
    {
        // When
        CommentMetrics aMetrics = new CommentMetrics();

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * Adding a null {@code CommentMetrics} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullCommentMetricsThrows()
    {
        // Given
        CommentMetrics aMetrics = null;

        // When
        new CommentMetrics().add(aMetrics);
    }


    /**
     * Adding a null {@code LineComment} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullLineCommentThrows()
    {
        // Given
        LineComment aComment = null;

        // When
        new CommentMetrics().add(aComment);
    }


    /**
     * Adding a null {@code BlockComment} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullBlockCommentThrows()
    {
        // Given
        BlockComment aComment = null;

        // When
        new CommentMetrics().add(aComment);
    }


    /**
     * Adding a null @code JavadocComment} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullJavaDocCommentThrows()
    {
        // Given
        JavadocComment aComment = null;

        // When
        new CommentMetrics().add(aComment);
    }


    /**
     * Adding a {@code CommentMetrics} should increment the count of all comment types with the
     * values from the added instance.
     */
    @Test
    public void addingCommentMetricsIncreasesCounts()
    {
        // Given
        CommentMetrics aMetrics =
                new Builder()
                .withNumLineComments(2)
                .withBlockComments(4, 6)
                .withJavaDocComments(8, 10)
                .build();
        CommentMetrics aMetricsToAdd =
                new Builder()
                .withNumLineComments(1)
                .withBlockComments(2, 3)
                .withJavaDocComments(4, 5)
                .build();

        // When
        aMetrics.add(aMetricsToAdd);

        // Then
        assertEquals(3, aMetrics.getNumLineComments());
        assertEquals(6, aMetrics.getNumBlockComments());
        assertEquals(9, aMetrics.getNumBlockCommentLines());
        assertEquals(12, aMetrics.getNumJavaDocComments());
        assertEquals(15, aMetrics.getNumJavaDocLines());
    }


    /**
     * Adding a {@code LineComment} should increase the count of line comments.
     */
    @Test
    public void addingLineCommentIncreasesCount()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        int aLineCommentCount = aMetrics.getNumLineComments();

        // When
        aMetrics.add(createLineComment(1, 0, 1, 2));

        // Then
        assertEquals(aLineCommentCount + 1, aMetrics.getNumLineComments());

        // When (a line comment that spans two lines)
        aMetrics.add(createLineComment(1, 0, 2, 2));

        // Then
        assertEquals(aLineCommentCount + 3, aMetrics.getNumLineComments());
    }


    /**
     * Adding a {@code LineComment} should make {@code isEmpty()} return false.
     */
    @Test
    public void addingLineCommentMakesInstanceNonEmpty()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        aMetrics.add(createLineComment(1, 0, 2, 2));

        // Then
        assertFalse(aMetrics.isEmpty());
    }


    /**
     * Adding a {@code BlockComment} should increase the count of block comments.
     */
    @Test
    public void addingBlockCommentIncreasesCount()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        int aBlockCommentCount = aMetrics.getNumBlockComments();
        int aBlockCommentLineCount = aMetrics.getNumBlockCommentLines();

        // When (a block comment with only one line)
        aMetrics.add(createBlockComment(1, 0, 1, 2));

        // Then
        assertEquals(aBlockCommentCount + 1, aMetrics.getNumBlockComments());
        assertEquals(aBlockCommentLineCount + 1, aMetrics.getNumBlockCommentLines());

        // When (a block comment that spans four lines)
        aMetrics.add(createBlockComment(1, 0, 4, 2));

        // Then
        assertEquals(aBlockCommentCount + 2, aMetrics.getNumBlockComments());
        assertEquals(aBlockCommentLineCount + 5, aMetrics.getNumBlockCommentLines());
    }


    /**
     * Adding a {@code BlockComment} should make {@code isEmpty()} return false.
     */
    @Test
    public void addingBlockCommentMakesInstanceNonEmpty()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        aMetrics.add(createBlockComment(1, 0, 1, 2));

        // Then
        assertFalse(aMetrics.isEmpty());
    }


    /**
     * Adding a {@code JavaDocComment} should increase the count of JavDoc comments.
     */
    @Test
    public void addingJavaDocCommentIncreasesCount()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        int aJavaDocCommentCount = aMetrics.getNumJavaDocComments();
        int aJavaDocLineCount = aMetrics.getNumJavaDocLines();

        // When (a JavaDoc comment with only one line)
        aMetrics.add(createJavadocComment(7, 0, 7, 2));

        // Then
        assertEquals(aJavaDocCommentCount + 1, aMetrics.getNumJavaDocComments());
        assertEquals(aJavaDocLineCount + 1, aMetrics.getNumJavaDocLines());

        // When (a JavaDoc comment that spans eight lines)
        aMetrics.add(createJavadocComment(12, 0, 19, 100));

        // Then
        assertEquals(aJavaDocCommentCount + 2, aMetrics.getNumJavaDocComments());
        assertEquals(aJavaDocLineCount + 9, aMetrics.getNumJavaDocLines());
    }


    /**
     * Adding a {@code JavadocComment} should make {@code isEmpty()} return false.
     */
    @Test
    public void addingJavadocCommentMakesInstanceNonEmpty()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        aMetrics.add(createJavadocComment(12, 0, 19, 100));

        // Then
        assertFalse(aMetrics.isEmpty());
    }


    /**
     * Adding a a comment without a range should not increase the line count.
     */
    @Test
    public void addingCommentWithoutRangeDoesNotIncreaseLineCount()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        aMetrics.add(new BlockComment("/* */"));

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
    }


    /**
     * Builder for {@code CommentMetrics} instances to help create test scenarios.
     */
    static class Builder
    {
        int fNumLineComments;
        int fNumBlockComments;
        int fNumBlockCommentLines;
        int fNumJavaDocComments;
        int fNumJavaDocLines;

        CommentMetrics build()
        {
            CommentMetrics aMetrics = new CommentMetrics();

            // Add n line comments with 1 line.
            for (int i = 0; i< fNumLineComments; i++)
                aMetrics.add(createLineComment(i+1, 0, i+1, 1));

            // Add n-1 block comments with 1 line.
            for (int i = 0; i< fNumBlockComments - 1; i++)
                aMetrics.add(createBlockComment(i+1, 0, i+1, 3));

            // Add one block comment with the remaining lines.
            int aNumLines = fNumBlockCommentLines - fNumBlockComments;
            aMetrics.add(createBlockComment(1, 0, 1 + aNumLines, 3));

            // Add n-1 JavaDoc comments with 1 line.
            for (int i = 0; i< fNumJavaDocComments - 1; i++)
                aMetrics.add(createJavadocComment(i+1, 0, i+1, 4));

            // Add one JavaDoc comment with the remaining lines.
            aNumLines = fNumJavaDocLines - fNumJavaDocComments;
            aMetrics.add(createJavadocComment(1, 0, 1 + aNumLines, 4));

            return aMetrics;
        }

        Builder withNumLineComments(int pNumLineComments)
        {
            fNumLineComments = pNumLineComments;
            return this;
        }

        Builder withBlockComments(int pNumComments, int pNumLines)
        {
            fNumBlockComments = pNumComments;
            fNumBlockCommentLines = pNumLines;
            return this;
        }

        Builder withJavaDocComments(int pNumComments, int pNumLines)
        {
            fNumJavaDocComments = pNumComments;
            fNumJavaDocLines = pNumLines;
            return this;
        }
    }
}
