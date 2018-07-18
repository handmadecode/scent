/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.util.Arrays;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Name;
import static com.github.javaparser.Range.range;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.CommentMetrics;

import static org.myire.scent.util.CommentTestUtil.createBlockComment;
import static org.myire.scent.util.CommentTestUtil.createJavadocComment;
import static org.myire.scent.util.CommentTestUtil.createLineComment;


/**
 * Unit tests related to parsing and collecting metrics for comments.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class CommentCollectTest
{
    /**
     * Calling {@code moveNodeComments()} should move the main comment from the source node to the
     * target node if the target doesn't have a main comment.
     */
    @Test
    public void mainCommentIsMovedIfTargetHasNoMainComment()
    {
        // Given
        Comment aComment = createBlockComment(2);
        Node aSource = createNodeWithComment(aComment);
        Node aTarget = createNodeWithoutComment();

        // When
        Collectors.moveNodeComments(aSource, aTarget);

        // Then
        assertFalse(aSource.getComment().isPresent());
        assertSame(aComment, aTarget.getComment().get());
    }


    /**
     * Calling {@code moveNodeComments()} should not move the main comment from the source node to
     * the target node if the target already has a main comment.
     */
    @Test
    public void mainCommentIsNotMovedIfTargetHasMainComment()
    {
        // Given
        Comment aComment = createBlockComment(7);
        Node aSource = createNodeWithComment(aComment);
        Node aTarget = createNodeWithComment(createBlockComment(5));

        // When
        Collectors.moveNodeComments(aSource, aTarget);

        // Then
        assertSame(aComment, aSource.getComment().get());
        assertNotSame(aComment, aTarget.getComment().get());
    }


    /**
     * Calling {@code moveNodeComments()} should move all orphan comments from the source to the
     * target.
     */
    @Test
    public void orphanCommentsAreMoved()
    {
        // Given
        Comment aComment1 = createBlockComment(28);
        Comment aComment2 = createLineComment();
        Comment aComment3 = new JavadocComment();
        Node aSource = createNodeWithOrphans(aComment1, aComment2);
        Node aTarget = createNodeWithOrphans(aComment3);

        // When
        Collectors.moveNodeComments(aSource, aTarget);

        // Then
        assertTrue(aSource.getOrphanComments().isEmpty());
        assertTrue(aTarget.getOrphanComments().containsAll(Arrays.asList(aComment1, aComment2, aComment3)));
    }


    /**
     * {@code collectNodeComment()} should collect metrics for a node's main comment.
     */
    @Test
    public void nodeCommentIsCollected()
    {
        // Given
        Node aNode = createNodeWithComment(createLineComment());
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComment(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertFalse(aNode.getComment().isPresent());
    }


    /**
     * {@code collectNodeComment()} should not collect any metrics for a node without a main
     * comment.
     */
    @Test
    public void missingNodeCommentIsNotCollected()
    {
        // Given
        Node aNode = createNodeWithoutComment();
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComment(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * {@code collectNodeComments()} should collect metrics for a node's main comment.
     */
    @Test
    public void mainCommentIsCollected()
    {
        // Given
        int aNumBlockCommentLines = 2;
        Node aNode = createNodeWithComment(createBlockComment(aNumBlockCommentLines));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(aNumBlockCommentLines, aMetrics.getNumBlockCommentLines());
        assertFalse(aNode.getComment().isPresent());
    }


    /**
     * {@code collectNodeComments()} should collect metrics for a node's orphan comment.
     */
    @Test
    public void orphanCommentsAreCollected()
    {
        // Given
        int aNumBlockCommentLines = 4711;
        int aNumJavaDocLines = 17;
        Node aNode = createNodeWithOrphans(
                createBlockComment(aNumBlockCommentLines),
                createJavadocComment(aNumJavaDocLines));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(aNumBlockCommentLines, aMetrics.getNumBlockCommentLines());
        assertEquals(1, aMetrics.getNumJavaDocComments());
        assertEquals(aNumJavaDocLines, aMetrics.getNumJavaDocLines());
        assertTrue(aNode.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectNodeComments()} should collect metrics for a node's JavaDoc comment.
     */
    @Test
    public void javaDocCommentIsCollected()
    {
        // Given
        int aNumJavaDocLines = 3;
        Node aNode = createNodeWithComment(createJavadocComment(aNumJavaDocLines));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumJavaDocComments());
        assertEquals(aNumJavaDocLines, aMetrics.getNumJavaDocLines());
        assertFalse(aNode.getComment().isPresent());
    }


    /**
     * {@code collectNodeComments()} should not collect any metrics for a node with no comments.
     */
    @Test
    public void nothingIsCollectedForNodeWithoutComments()
    {
        // Given
        Node aNode = createNodeWithoutComment();
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * {@code collectChildComments()} should collect comments metrics for a node's children.
     */
    @Test
    public void childCommentsAreCollected()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        int aNumBlockCommentLines = 17;
        int aNumJavaDocLines = 4;
        Node aGrandChild = createNodeWithComment(createJavadocComment(aNumJavaDocLines));
        Node aChild = createNodeWithComment(createLineComment());
        aChild.addOrphanComment(createBlockComment(aNumBlockCommentLines));
        Node aNode = createNodeWithoutComment();

        aGrandChild.setParentNode(aChild);
        aChild.setParentNode(aNode);

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(aNumBlockCommentLines, aMetrics.getNumBlockCommentLines());
        assertEquals(1, aMetrics.getNumJavaDocComments());
        assertEquals(aNumJavaDocLines, aMetrics.getNumJavaDocLines());

        assertFalse(aGrandChild.getComment().isPresent());
        assertFalse(aChild.getComment().isPresent());
        assertTrue(aChild.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectChildComments()} should not collect any metrics for a node with no children.
     */
    @Test
    public void noChildCommentsAreCollectedForNodeWithoutChildren()
    {
        // Given
        Node aNode = createNodeWithoutComment();
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * {@code collectChildComments()} should not collect any metrics for a node whose children have
     * no comments.
     */
    @Test
    public void noChildCommentsAreCollectedForNodeWithChildrenWithoutComments()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        Node aNode = createNodeWithoutComment();
        Node aChild = createNodeWithoutComment();
        aChild.setParentNode(aNode);

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * {@code collectChildComments()} should not collect any metrics for the starting node.
     */
    @Test
    public void nodeCommentIsNotCollectAsChildComment()
    {
        // Given
        CommentMetrics aMetrics = new CommentMetrics();
        LineComment aComment = createLineComment();
        Node aNode = createNodeWithComment(aComment);
        Node aChild = createNodeWithoutComment();
        aChild.setParentNode(aNode);

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
        assertSame(aComment, aNode.getComment().get());
    }


    /**
     * {@code collectParentOrphanComments()} should collect parent orphans that immediately precede
     * the node's main comment.
     */
    @Test
    public void parentOrphansThatPrecedeMainCommentAreCollected()
    {
        // Given
        // aComment2 immediately precedes the node's main comment
        // aComment1 immediately precedes aComment2
        LineComment aComment1 = createLineComment(1, 1, 1, 2);
        LineComment aComment2 = createLineComment(2, 1, 2, 2);
        Node aParent = createNodeWithOrphans(aComment1, aComment2);
        LineComment aComment3 = createLineComment(3, 1, 3, 2);
        Node aNode = createNodeWithComment(aComment3);
        aNode.setParentNode(aParent);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then (the orphans  but not the main comment should have been collected)
        assertEquals(2, aMetrics.getNumLineComments());
        assertTrue(aParent.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectParentOrphanComments()} should collect multiple parent orphans on the same line
     * if they immediately precede the node's main comment.
     */
    @Test
    public void parentOrphansOnSameLineAreCollected()
    {
        // Given
        // The parent orphans have the layout
        /* Comment1 */ /* Comment2 */ /* Comment3
           spans two lines */
        BlockComment aComment1 = createBlockComment(2, 1, 2, 14, " Comment1 ");
        BlockComment aComment2 = createBlockComment(2, 16, 2, 29, " Comment2 ");
        BlockComment aComment3 = createBlockComment(2, 31, 3, 21, " Comment3\n   spans two lines ");
        Node aParent = createNodeWithOrphans(aComment1, aComment2, aComment3);
        LineComment aNodeComment = createLineComment(4, 2, 4, 5);
        Node aNode = createNodeWithComment(aNodeComment);
        CommentMetrics aMetrics = new CommentMetrics();
        aNode.setParentNode(aParent);

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(3, aMetrics.getNumBlockComments());
        assertEquals(4, aMetrics.getNumBlockCommentLines());
        assertTrue(aParent.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectParentOrphanComments()} should collect a parent orphan comment that is located
     * on the same line as the node's main comment.
     */
    @Test
    public void parentOrphanOnSameLineAsNodeCommentAreCollected()
    {
        // Given
        BlockComment aOrphan = createBlockComment(10, 3, 10, 17, "");
        Node aParent = createNodeWithOrphans(aOrphan);
        LineComment aNodeComment = createLineComment(10, 20, 10, 31);
        Node aNode = createNodeWithComment(aNodeComment);
        aNode.setParentNode(aParent);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(1, aMetrics.getNumBlockCommentLines());
        assertTrue(aParent.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectParentOrphanComments()} should collect parent orphan comments that have
     * overlapping ranges. This is a contrived scenario but such an AST is nevertheless possible to
     * construct.
     */
    @Test
    public void overlappingParentOrphansAreCollected()
    {
        // Given
        BlockComment aOrphan1 = createBlockComment(10, 3, 10, 15);
        LineComment aOrphan2 = createLineComment(10, 3, 10, 8);
        JavadocComment aOrphan3 = createJavadocComment(10, 20, 10, 31);
        JavadocComment aOrphan4 = createJavadocComment(10, 20, 10, 31);
        LineComment aOrphan5 = createLineComment(10, 20, 10, 52);
        Node aParent = createNodeWithOrphans(aOrphan1, aOrphan2, aOrphan3, aOrphan4, aOrphan5);
        Node aNode = createNodeWithComment(createLineComment(11, 1, 11, 12));
        aNode.setParentNode(aParent);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(2, aMetrics.getNumLineComments());
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(1, aMetrics.getNumBlockCommentLines());
        assertEquals(2, aMetrics.getNumJavaDocComments());
        assertEquals(2, aMetrics.getNumJavaDocLines());
        assertTrue(aParent.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectParentOrphanComments()} should not collect parent orphans that don't precede
     * the node's main comment.
     */
    @Test
    public void parentOrphansThatDoNotPrecedeMainCommentAreNotCollected()
    {
        // Given
        // only aComment3 immediately precedes the node's comment
        LineComment aComment1 = createLineComment(1, 1, 1, 2);
        LineComment aComment3 = createLineComment(3, 7, 3, 10);
        LineComment aComment17 = createLineComment(17, 11, 17, 26);
        Node aParent = createNodeWithOrphans(aComment1, aComment3, aComment17);
        LineComment aNodeComment = createLineComment(4, 1, 4, 2);
        Node aNode = createNodeWithComment(aNodeComment);
        aNode.setParentNode(aParent);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertTrue(aParent.getOrphanComments().containsAll(Arrays.asList(aComment1, aComment17)));
    }


    /**
     * {@code collectParentOrphanComments()} should not collect parent orphans that don't have a
     * range and thus cannot be determined to be adjacent to the child node.
     */
    @Test
    public void parentOrphansWithoutRangeAreNotCollected()
    {
        // Given (only aComment2 has a range)
        LineComment aComment1 = new LineComment("");
        LineComment aComment2 = createLineComment(3, 1, 3, 2);
        LineComment aComment3 = new LineComment("");
        Node aParent = createNodeWithOrphans(aComment1, aComment2, aComment3);
        LineComment aNodeComment = createLineComment(4, 1, 4, 2);
        Node aNode = createNodeWithComment(aNodeComment);
        aNode.setParentNode(aParent);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertTrue(aParent.getOrphanComments().containsAll(Arrays.asList(aComment1, aComment3)));
    }


    /**
     * {@code collectParentOrphanComments()} should not attempt to collect parent orphans for a node
     * that doesn't have a parent.
     */
    @Test
    public void parentOrphansAreNotCollectedForNodeWithoutParent()
    {
        Node aNode = createNodeWithComment(createBlockComment(2));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * {@code collectParentOrphanComments()} should not attempt to collect parent orphans for a node
     * that doesn't have a range since it cannot be determined which comments are adjacent to it.
     */
    @Test
    public void parentOrphansAreNotCollectedForNodeWithoutRange()
    {
        // Given
        Node aNode = createNodeWithComment(createBlockComment(2));
        aNode.setParentNode(createNodeWithoutComment());
        aNode.setRange(null);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * Create a {@code Node} without a comment.
     *
     * @return  A new {@code Node}.
     */
    static private Node createNodeWithoutComment()
    {
        return new PackageDeclaration(new Name("pkg"));
    }


    /**
     * Create a {@code Node} instance that has a comment.
     *
     * @param pComment  The node's comment.
     *
     * @return  A new {@code Node}.
     */
    static private Node createNodeWithComment(Comment pComment)
    {
        Node aNode = createNodeWithoutComment();
        aNode.setComment(pComment);

        pComment.getRange().ifPresent(
                r -> aNode.setRange(range(r.end.line + 1, r.begin.column, r.end.line + 1, r.end.column))
        );

        return aNode;
    }


    /**
     * Create a {@code Node} instance that initially contains some orphan comments.
     *
     * @param pOrphans  The node's initial orphan comments.
     *
     * @return  A new {@code Node}.
     */
    static private Node createNodeWithOrphans(Comment... pOrphans)
    {
        Node aNode = createNodeWithoutComment();
        for (Comment aOrphan : pOrphans)
            aNode.addOrphanComment(aOrphan);

        return aNode;
    }
}
