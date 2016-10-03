/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.util.Arrays;

import com.github.javaparser.ast.DocumentableNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;

import com.github.javaparser.ast.expr.NameExpr;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.CommentMetrics;


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
        Comment aComment = new BlockComment();
        Node aSource = createPlainNode(aComment);
        Node aTarget = createPlainNode(null);

        // When
        Collectors.moveNodeComments(aSource, aTarget);

        // Then
        assertNull(aSource.getComment());
        assertSame(aComment, aTarget.getComment());
    }


    /**
     * Calling {@code moveNodeComments()} should not move the main comment from the source node to
     * the target node if the target already has a main comment.
     */
    @Test
    public void mainCommentIsNotMovedIfTargetHasMainComment()
    {
        // Given
        Comment aComment = new BlockComment();
        Node aSource = createPlainNode(aComment);
        Node aTarget = createPlainNode(new BlockComment());

        // When
        Collectors.moveNodeComments(aSource, aTarget);

        // Then
        assertSame(aComment, aSource.getComment());
        assertNotSame(aComment, aTarget.getComment());
    }


    /**
     * Calling {@code moveNodeComments()} should move all orphan comments from the source to the
     * target.
     */
    @Test
    public void orphanCommentsAreMoved()
    {
        // Given
        Comment aComment1 = new BlockComment();
        Comment aComment2 = new LineComment();
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
     * Calling {@code moveNodeComments()} should move the JavaDoc comment from the source node to
     * the target node if the target doesn't have a JavaDoc comment.
     */
    @Test
    public void javaDocCommentIsMovedIfTargetHasNoJavaDocComment()
    {
        // Given
        JavadocComment aComment = new JavadocComment();
        DocumentableNode aSource = createDocumentableNode(aComment);
        DocumentableNode aTarget = createDocumentableNode(null);

        // When
        Collectors.moveNodeComments((Node) aSource, (Node) aTarget);

        // Then
        assertNull(aSource.getJavaDoc());
        assertSame(aComment, aTarget.getJavaDoc());
    }


    /**
     * Calling {@code moveNodeComments()} should not move the JavaDoc comment from the source node
     * to the target node if the target already has a JavaDoc comment.
     */
    @Test
    public void javaDocCommentIsNotMovedIfTargetHasJavaDocComment()
    {
        // Given
        JavadocComment aComment = new JavadocComment();
        DocumentableNode aSource = createDocumentableNode(aComment);
        DocumentableNode aTarget = createDocumentableNode(new JavadocComment());

        // When
        Collectors.moveNodeComments((Node) aSource, (Node) aTarget);

        // Then
        assertSame(aComment, aSource.getJavaDoc());
        assertNotSame(aComment, aTarget.getJavaDoc());
    }


    /**
     * Calling {@code moveNodeComments()} should not attempt to move the JavaDoc comment from the
     * source node to the target node if the source isn't a {@code DocumentableNode}.
     */
    @Test
    public void javaDocCommentIsNotMovedIfSourceIsNotDocumentable()
    {
        // Given
        JavadocComment aComment = new JavadocComment();
        Node aSource = createPlainNode(aComment);
        DocumentableNode aTarget = createDocumentableNode(null);

        // When
        Collectors.moveNodeComments(aSource, (Node) aTarget);

        // Then
        assertNull(aTarget.getJavaDoc());
    }


    /**
     * Calling {@code moveNodeComments()} should not move the JavaDoc comment from the source node
     * to the target node if the target isn't a {@code DocumentableNode}.
     */
    @Test
    public void javaDocCommentIsNotMovedIfTargetIsNotDocumentable()
    {
        // Given
        JavadocComment aComment = new JavadocComment();
        DocumentableNode aSource = createDocumentableNode(aComment);
        Node aTarget = createPlainNode(null);

        // When
        Collectors.moveNodeComments((Node) aSource, aTarget);

        // Then
        assertSame(aComment, aSource.getJavaDoc());
    }


    /**
     * {@code collectNodeComment()} should collect metrics for a node's main comment.
     */
    @Test
    public void nodeCommentIsCollected()
    {
        // Given
        Node aNode = createPlainNode(createLineComment());
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComment(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertNull(aNode.getComment());
    }


    /**
     * {@code collectNodeComment()} should not collect any metrics for a node without a main
     * comment.
     */
    @Test
    public void missingNodeCommentIsNotCollected()
    {
        // Given
        Node aNode = createPlainNode(null);
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
        Node aNode = createPlainNode(createBlockComment(2));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(2, aMetrics.getNumBlockCommentLines());
        assertNull(aNode.getComment());
    }


    /**
     * {@code collectNodeComments()} should collect metrics for a node's orphan comment.
     */
    @Test
    public void orphanCommentsAreCollected()
    {
        // Given
        Node aNode = createNodeWithOrphans(createBlockComment(2), createLineComment());
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(2, aMetrics.getNumBlockCommentLines());
        assertEquals(1, aMetrics.getNumLineComments());
        assertTrue(aNode.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectNodeComments()} should collect metrics for a node's JavaDoc comment.
     */
    @Test
    public void javaDocCommentIsCollected()
    {
        // Given
        DocumentableNode aNode = createDocumentableNode(createJavaDocComment(3));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectNodeComments((Node) aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumJavaDocComments());
        assertEquals(3, aMetrics.getNumJavaDocLines());
        assertNull(aNode.getJavaDoc());
    }


    /**
     * {@code collectNodeComments()} should not collect any metrics for a node with no comments.
     */
    @Test
    public void nothingIsCollectedForNodeWithoutComments()
    {
        // Given
        Node aNode = createPlainNode(null);
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
        Node aGrandChild = (Node) createDocumentableNode(createJavaDocComment(4));
        Node aChild = createPlainNode(createLineComment());
        aChild.getOrphanComments().add(createBlockComment(17));
        aChild.getChildrenNodes().add(aGrandChild);
        Node aNode = createPlainNode(null);
        aNode.getChildrenNodes().add(aChild);
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertEquals(1, aMetrics.getNumBlockComments());
        assertEquals(17, aMetrics.getNumBlockCommentLines());
        assertEquals(1, aMetrics.getNumJavaDocComments());
        assertEquals(4, aMetrics.getNumJavaDocLines());
        assertNull(((DocumentableNode) aGrandChild).getJavaDoc());
        assertNull(aChild.getComment());
        assertTrue(aChild.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectChildComments()} should not collect any metrics for a node with no children.
     */
    @Test
    public void noChildCommentsAreCollectedForNodeWithoutChildren()
    {
        // Given
        Node aNode = createPlainNode(null);
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
        Node aNode = createPlainNode(null);
        aNode.getChildrenNodes().add(createPlainNode(null));
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
     * {@code collectChildComments()} should not collect any metrics for the starting node.
     */
    @Test
    public void nodeCommentIsNotCollectAsChildComment()
    {
        // Given
        LineComment aComment = createLineComment();
        Node aNode = createPlainNode(aComment);
        aNode.getChildrenNodes().add(createPlainNode(null));
        CommentMetrics aMetrics = new CommentMetrics();

        // When
        Collectors.collectChildComments(aNode, aMetrics);

        // Then
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
        assertSame(aComment, aNode.getComment());
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
        LineComment aComment1 = new LineComment(1, 1, 1, 2, "");
        LineComment aComment2 = new LineComment(2, 1, 2, 2, "");
        Node aParent = createNodeWithOrphans(aComment1, aComment2);
        LineComment aComment3 = new LineComment(3, 1, 3, 2, "");
        Node aNode = createPlainNode(aComment3);
        CommentMetrics aMetrics = new CommentMetrics();
        aNode.setParentNode(aParent);

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(2, aMetrics.getNumLineComments());
        assertTrue(aParent.getOrphanComments().isEmpty());
    }


    /**
     * {@code collectParentOrphanComments()} should collect multiple parent orphans that immediately
     * precede the node's main comment on the same line.
     */
    @Test
    public void parentOrphansOnSameLineAreCollected()
    {
        // Given
        // The parent orphans have the layout
        /* Comment1 */ /* Comment2 */ /* Comment3
           spans two lines */
        BlockComment aComment1 = new BlockComment(1, 1, 1, 14, " Comment1 ");
        BlockComment aComment2 = new BlockComment(1, 16, 1, 29, " Comment2 ");
        BlockComment aComment3 = new BlockComment(1, 31, 2, 21, " Comment3\n   spans two lines ");
        Node aParent = createNodeWithOrphans(aComment1, aComment2, aComment3);
        LineComment aNodeComment = new LineComment(3, 1, 3, 2, "");
        Node aNode = createPlainNode(aNodeComment);
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
     * {@code collectParentOrphanComments()} should not collect parent orphans that that don't
     * precede the node's main comment.
     */
    @Test
    public void parentOrphansThatDoNotPrecedeMainCommentAreNotCollected()
    {
        // Given
        // only aComment3 immediately precedes the node's comment
        LineComment aComment1 = new LineComment(1, 1, 1, 2, "");
        LineComment aComment3 = new LineComment(3, 1, 3, 2, "");
        LineComment aComment17 = new LineComment(17, 1, 17, 2, "");
        Node aParent = createNodeWithOrphans(aComment1, aComment3, aComment17);
        LineComment aNodeComment = new LineComment(4, 1, 4, 2, "");
        Node aNode = createPlainNode(aNodeComment);
        CommentMetrics aMetrics = new CommentMetrics();
        aNode.setParentNode(aParent);

        // When
        Collectors.collectParentOrphanComments(aNode, aMetrics);

        // Then
        assertEquals(1, aMetrics.getNumLineComments());
        assertTrue(aParent.getOrphanComments().containsAll(Arrays.asList(aComment1, aComment17)));
    }


    /**
     * {@code collectParentOrphanComments()} should not attempt to collect parent orphans for a node
     * that doesn't have a parent.
     */
    @Test
    public void parentOrphansAreNotCollectedForNodeWithoutParent()
    {
        Node aNode = createPlainNode(createBlockComment(2));
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
     * Create a {@code LineComment}.
     *
     * @return  A new {@code LineComment}.
     */
    static private LineComment createLineComment()
    {
        return new LineComment(1, 1, 1, 2, "");
    }


    /**
     * Create a {@code BlockComment}.
     *
     * @param pNumLines The number of lines the block comment should span.
     *
     * @return  A new {@code BlockComment}.
     */
    static private BlockComment createBlockComment(int pNumLines)
    {
        return new BlockComment(1, 1, pNumLines, 4, "");
    }


    /**
     * Create a {@code JavadocComment}.
     *
     * @param pNumLines The number of lines the JavaDoc comment should span.
     *
     * @return  A new {@code JavadocComment}.
     */
    static private JavadocComment createJavaDocComment(int pNumLines)
    {
        return new JavadocComment(1, 1, pNumLines, 6, "");
    }


    /**
     * Create a {@code Node} instance that does not implements {@code DocumentableNode}.
     *
     * @param pComment  The node's initial main comment.
     *
     * @return  A new {@code Node}.
     */
    static private Node createPlainNode(Comment pComment)
    {
        Node aNode = new PackageDeclaration(new NameExpr("pkg"));
        aNode.setComment(pComment);
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
        Node aNode = new PackageDeclaration(new NameExpr("pkg"));
        aNode.getOrphanComments().addAll(Arrays.asList(pOrphans));
        return aNode;
    }


    /**
     * Create a {@code Node} instance that implements {@code DocumentableNode}.
     *
     * @param pComment  The node's initial JavaDoc comment.
     *
     * @return  A new {@code DocumentableNode}.
     */
    static private DocumentableNode createDocumentableNode(JavadocComment pComment)
    {
        DocumentableNode aNode = new EmptyMemberDeclaration(1, 1, 1, 1);
        aNode.setJavaDoc(pComment);
        return aNode;
    }
}
