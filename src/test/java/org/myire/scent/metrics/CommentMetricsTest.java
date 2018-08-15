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
        assertEquals(0, aMetrics.getLineCommentsLength());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getBlockCommentsLength());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
        assertEquals(0, aMetrics.getJavaDocCommentsLength());
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
                .withLineComments(2, 17)
                .withBlockComments(4, 6, 12)
                .withJavaDocComments(8, 10, 29)
                .build();
        CommentMetrics aMetricsToAdd =
                new Builder()
                .withLineComments(1, 5)
                .withBlockComments(2, 3, 21)
                .withJavaDocComments(4, 5, 19)
                .build();

        // When
        aMetrics.add(aMetricsToAdd);

        // Then
        assertEquals(3, aMetrics.getNumLineComments());
        assertEquals(22, aMetrics.getLineCommentsLength());
        assertEquals(6, aMetrics.getNumBlockComments());
        assertEquals(9, aMetrics.getNumBlockCommentLines());
        assertEquals(33, aMetrics.getBlockCommentsLength());
        assertEquals(12, aMetrics.getNumJavaDocComments());
        assertEquals(15, aMetrics.getNumJavaDocLines());
        assertEquals(48, aMetrics.getJavaDocCommentsLength());
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
        int aLineCommentLength = aMetrics.getLineCommentsLength();

        // When
        int aLength1 = 26;
        aMetrics.add(createLineComment(1, 0, 1, 2, aLength1));

        // Then
        assertEquals(aLineCommentCount + 1, aMetrics.getNumLineComments());
        assertEquals(aLineCommentLength + aLength1, aMetrics.getLineCommentsLength());

        // When (a line comment that spans two lines)
        int aLength2 = 14;
        aMetrics.add(createLineComment(1, 0, 2, 2, aLength2));

        // Then
        assertEquals(aLineCommentCount + 3, aMetrics.getNumLineComments());
        assertEquals(aLineCommentLength + aLength1 + aLength2, aMetrics.getLineCommentsLength());
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
        aMetrics.add(createLineComment());

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
        int aBlockCommentLength = aMetrics.getBlockCommentsLength();

        // When (a block comment with only one line)
        int aLines1 = 1;
        int aLength1 = 28;
        aMetrics.add(createBlockComment(1, 0, aLines1, 2, aLength1));

        // Then
        assertEquals(aBlockCommentCount + 1, aMetrics.getNumBlockComments());
        assertEquals(aBlockCommentLineCount + aLines1, aMetrics.getNumBlockCommentLines());
        assertEquals(aBlockCommentLength + aLength1, aMetrics.getBlockCommentsLength());

        // When (a block comment that spans four lines)
        int aLines2 = 4;
        int aLength2 = 102;
        aMetrics.add(createBlockComment(1, 0, aLines2, 2, aLength2));

        // Then
        assertEquals(aBlockCommentCount + 2, aMetrics.getNumBlockComments());
        assertEquals(aBlockCommentLineCount + aLines1 + aLines2, aMetrics.getNumBlockCommentLines());
        assertEquals(aBlockCommentLength + aLength1 + aLength2, aMetrics.getBlockCommentsLength());
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
        aMetrics.add(createBlockComment(1));

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
        int aJavaDocCommentLength = aMetrics.getJavaDocCommentsLength();

        // When (a JavaDoc comment with only one line)
        int aLines1 = 1;
        int aLength1 = 6;
        aMetrics.add(createJavadocComment(1, 0, aLines1, 2, aLength1));

        // Then
        assertEquals(aJavaDocCommentCount + 1, aMetrics.getNumJavaDocComments());
        assertEquals(aJavaDocLineCount + aLines1, aMetrics.getNumJavaDocLines());
        assertEquals(aJavaDocCommentLength + aLength1, aMetrics.getJavaDocCommentsLength());

        // When (a JavaDoc comment that spans eight lines)
        int aLines2 = 8;
        int aLength2 = 72;
        aMetrics.add(createJavadocComment(1, 0, aLines2, 100, aLength2));

        // Then
        assertEquals(aJavaDocCommentCount + 2, aMetrics.getNumJavaDocComments());
        assertEquals(aJavaDocLineCount + aLines1 + aLines2, aMetrics.getNumJavaDocLines());
        assertEquals(aJavaDocCommentLength + aLength1 + aLength2, aMetrics.getJavaDocCommentsLength());
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
        aMetrics.add(createJavadocComment(12));

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
        aMetrics.add(new BlockComment(""));

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
    }


    /**
     * Leading and trailing whitespace in a line comment's content should not add to the length of
     * the line comments in the metrics.
     */
    @Test
    public void leadingAndTrailingWhitespaceDoesNotAddToLineCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithWhitespace = "\t" + aContent + "  ";

        // When
        aMetrics.add(createLineComment(1, 1, 1, aContentWithWhitespace.length(), aContentWithWhitespace));

        // Then
        assertEquals(aContent.length(), aMetrics.getLineCommentsLength());
    }


    /**
     * Leading and trailing whitespace in a block comment's content should not add to the length of
     * the block comments in the metrics.
     */
    @Test
    public void leadingAndTrailingWhitespaceDoesNotAddToBlockCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithWhitespace = "\t \t" + aContent + "    ";

        // When
        aMetrics.add(createBlockComment(1, 1, 1, aContentWithWhitespace.length(), aContentWithWhitespace));

        // Then
        assertEquals(aContent.length(), aMetrics.getBlockCommentsLength());
    }


    /**
     * Leading and trailing whitespace in a JavaDoc comment's content should not add to the length
     * of the JavaDoc comments in the metrics.
     */
    @Test
    public void leadingAndTrailingWhitespaceDoesNotAddToJavaDocCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithWhitespace = " " + aContent + "  \t  ";

        // When
        aMetrics.add(createJavadocComment(1, 1, 1, aContentWithWhitespace.length(), aContentWithWhitespace));

        // Then
        assertEquals(aContent.length(), aMetrics.getJavaDocCommentsLength());
    }


    /**
     * Line breaks in a block comment's content should not add to the length of the block comments
     * in the metrics.
     */
    @Test
    public void lineBreaksDoNotAddToBlockCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithLineBreaks = "\r" + aContent + "\r\n" + aContent + "\n" + aContent + "\r\n ";

        // When
        aMetrics.add(createBlockComment(1, 1, 4, aContentWithLineBreaks.length(), aContentWithLineBreaks));

        // Then
        assertEquals(aContent.length() * 3, aMetrics.getBlockCommentsLength());
    }


    /**
     * Line breaks in a JavaDoc comment's content should not add to the length of the JavaDoc
     * comments in the metrics.
     */
    @Test
    public void lineBreaksDoNotAddToJavaDocCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithLineBreaks = "\r" + aContent + "\r\n" + aContent;

        // When
        aMetrics.add(createJavadocComment(1, 1, 3, aContentWithLineBreaks.length(), aContentWithLineBreaks));

        // Then
        assertEquals(aContent.length() * 2, aMetrics.getJavaDocCommentsLength());
    }


    /**
     * Leading and trailing asterisks in a block comment's content should not add to the length of
     * the block comments in the metrics.
     */
    @Test
    public void leadingAndTrailingAsterisksDoNotAddToBlockCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithAsterisks = "\r*" + aContent + "*\r\n *" + aContent + " * ";

        // When
        aMetrics.add(createBlockComment(1, 1, 3, aContentWithAsterisks.length(), aContentWithAsterisks));

        // Then
        assertEquals(aContent.length() * 2, aMetrics.getBlockCommentsLength());
    }


    /**
     * Leading and trailing asterisks in a JavaDoc comment's content should not add to the length of
     * the JavaDoc comments in the metrics.
     */
    @Test
    public void leadingAndTrailingAsterisksDoNotAddToJavaDocCommentsLength()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        String aContent = "content";
        String aContentWithAsterisks = "\r*" + aContent + "*\r\n *" + aContent + " * \n**" + aContent;

        // When
        aMetrics.add(createJavadocComment(1, 1, 3, aContentWithAsterisks.length(), aContentWithAsterisks));

        // Then
        assertEquals(aContent.length() * 3, aMetrics.getJavaDocCommentsLength());
    }


    /**
     * Builder for {@code CommentMetrics} instances to help create test scenarios.
     */
    static class Builder
    {
        int fNumLineComments;
        int fLineCommentsLength;
        int fNumBlockComments;
        int fNumBlockCommentLines;
        int fBlockCommentsLength;
        int fNumJavaDocComments;
        int fNumJavaDocLines;
        int fJavaDocCommentsLength;

        CommentMetrics build()
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


        Builder withLineComments(int pNumComments)
        {
            return withLineComments(pNumComments, 0);
        }

        Builder withLineComments(int pNumComments, int pLength)
        {
            fNumLineComments = pNumComments;
            fLineCommentsLength = pLength;
            return this;
        }

        Builder withBlockComments(int pNumComments, int pNumLines)
        {
            return withBlockComments(pNumComments, pNumLines, 0);
        }

        Builder withBlockComments(int pNumComments, int pNumLines, int pLength)
        {
            fNumBlockComments = pNumComments;
            fNumBlockCommentLines = pNumLines;
            fBlockCommentsLength = pLength;
            return this;
        }

        Builder withJavaDocComments(int pNumComments, int pNumLines)
        {
            return withJavaDocComments(pNumComments, pNumLines, 0);
        }

        Builder withJavaDocComments(int pNumComments, int pNumLines, int pLength)
        {
            fNumJavaDocComments = pNumComments;
            fNumJavaDocLines = pNumLines;
            fJavaDocCommentsLength = pLength;
            return this;
        }
    }
}
