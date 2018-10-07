/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.StatementMetrics;


/**
 * Utility methods for collectors of source code metrics.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
final class Collectors
{
    // A range with negative coordinates to use as placeholder for nodes that don't have an explicit
    // range.
    static private final Range NO_RANGE = Range.range(-1, -1, -1, -1);


    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private Collectors()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Move the comments from one node to another. The main comment will not be moved if the target
     * node already has a main comment.
     *
     * @param pSourceNode   The node to move the comments from.
     * @param pTargetNode   The node to move the comments to.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void moveNodeComments(@Nonnull Node pSourceNode, @Nonnull Node pTargetNode)
    {
        // Move the main comment if not present in the target node.
        if (!pTargetNode.getComment().isPresent())
        {
            pSourceNode.getComment().ifPresent(pTargetNode::setComment);
            pSourceNode.setComment(null);
        }

        // Move any orphan comments.
        for (Comment aOrphan : pSourceNode.getOrphanComments())
        {
            pTargetNode.addOrphanComment(aOrphan);
            pSourceNode.removeOrphanComment(aOrphan);
        }
    }


    /**
     * Collect the primary comment from a node and remove it to prevent it from being collected
     * again.
     *
     * @param pNode     The node to collect and remove the comment from.
     * @param pMetrics  Where to put the collected metrics.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void collectNodeComment(@Nonnull Node pNode, @Nonnull CommentMetrics pMetrics)
    {
        // In some rare cases (like some of the instances of
        // MethodWithBodyCollectTestBase.lineCommentOnSameLineAsMethodSignatureIsCollected),
        // the AST will have nodes with comments where the comment refers back to another node.
        // To avoid collecting those comments multiple times a comment is only collected if it
        // refers back to the node that has it as comment.
        pNode.getComment().ifPresent(
            c ->
            {
                if (pNode == c.getCommentedNode().orElse(pNode))
                {
                    // The commented node is the one for which the comment is being collected.
                    c.accept(CommentVisitor.SINGLETON, pMetrics);
                    pNode.setComment(null);
                }
            }
        );
    }



    /**
     * Collect the comments from a node, including its orphan comments and JavaDoc comment. Each
     * collected comment will be removed from the node to prevent it from being collected again as
     * a child comment of the node's parent.
     *
     * @param pNode     The node to collect the comments from.
     * @param pMetrics  Where to put the collected metrics.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void collectNodeComments(@Nonnull Node pNode, @Nonnull CommentMetrics pMetrics)
    {
        // Collect and remove the main comment.
        collectNodeComment(pNode, pMetrics);

        // Collect and remove any orphan comments.
        for (Comment aOrphan : pNode.getOrphanComments())
        {
            aOrphan.accept(CommentVisitor.SINGLETON, pMetrics);
            pNode.removeOrphanComment(aOrphan);
        }
    }


    /**
     * Collect and remove the comments from a node's children. The comments belonging to the node
     * itself will <b>not</b> be collected.
     *
     * @param pNode     The node to collect all child comments from.
     * @param pMetrics  Where to put the collected metrics.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void collectChildComments(@Nonnull Node pNode, @Nonnull CommentMetrics pMetrics)
    {
        for (Node aChild : pNode.getChildNodes())
        {
            collectNodeComments(aChild, pMetrics);
            collectChildComments(aChild, pMetrics);
        }
    }


    /**
     * Collect the orphan comments from a node's parent that are adjacent to the node or its
     * comment. Orphan comments that immediately precede the node or its comment are considered to
     * be adjacent to the node. The same goes for an orphan comment that begins on the same line as
     * the node ends on.
     *<p>
     * The collected comments will be removed from the parent to prevent them from being collected
     * twice when the parent's comments are collected in a call to
     * {@link #collectNodeComments(Node, CommentMetrics)}.
     *
     * @param pNode     The node to collect adjacent parent orphan comments for.
     * @param pMetrics  Where to put the collected metrics.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void collectAdjacentParentOrphanComments(@Nonnull Node pNode, @Nonnull CommentMetrics pMetrics)
    {
        collectAdjacentParentComments(pNode, pMetrics, true);
    }


    /**
     * Collect the comments from a node's parent that are adjacent to the node or its comment.
     * Comments belonging to the parent that immediately precede the node or its comment are
     * considered to be adjacent to the node. The same goes for a parent comment that begins on the
     * same line as the node ends on.
     *<p>
     * The collected comments will be removed from the parent to prevent them from being collected
     * twice when the parent's comments are collected in a call to
     * {@link #collectNodeComments(Node, CommentMetrics)}.
     *
     * @param pNode         The node to collect adjacent parent comments for.
     * @param pMetrics      Where to put the collected metrics.
     * @param pOrphansOnly  If true, only the parent's orphan comments will be examined.
     *
     * @throws NullPointerException if any of the reference parameters is null.
     */
    static void collectAdjacentParentComments(
        @Nonnull Node pNode,
        @Nonnull CommentMetrics pMetrics,
        boolean pOrphansOnly)
    {
        Node aParent = pNode.getParentNode().orElse(null);
        Range aRange = pNode.getRange().orElse(null);
        if (aParent == null || aRange == null)
            // If the node doesn't have a parent there are no parent comments to collect; if the
            // node doesn't have a range there is no way of telling which of the parent's comments
            // that are adjacent to it.
            return;

        // Get the begin and end line of the node for which comment metrics are collected.
        int aBeginLine = aRange.begin.line;
        int aEndLine = aRange.end.line;

        // Adjust the begin line to include any comment on the node.
        Comment aComment = pNode.getComment().orElse(null);
        if (aComment != null)
            aBeginLine = aComment.getRange().orElse(aRange).begin.line;

        // Get the parent's comments.
        List<Comment> aParentComments = aParent.getOrphanComments();
        Comment aParentMainComment = pOrphansOnly ? null : aParent.getComment().orElse(null);
        if (aParentMainComment != null)
            aParentComments.add(aParentMainComment);

        // Sort the parent's comments on their position in the enclosing compilation unit.
        aParentComments.sort(CommentComparator.SINGLETON);

        // Examine the parent's comments starting from the bottom of the enclosing compilation unit.
        ListIterator<Comment> aIterator = aParentComments.listIterator(aParentComments.size());
        while (aIterator.hasPrevious())
        {
            Comment aParentComment = aIterator.previous();
            Range aParentCommentRange = aParentComment.getRange().orElse(NO_RANGE);
            if (aParentCommentRange.end.line < aBeginLine - 1)
                // The parent comment ends on a line not immediately preceding the node being
                // examined; there are no more parent comments adjacent to that node.
                break;

            if (aParentCommentRange.begin.line > aEndLine)
                // This parent comment begins after the node being examined, continue with the
                // previous parent comment, which is located above the current one.
                continue;

            // The parent comment either ends on the same line or the line immediately preceding the
            // node, or begins on the same line as the node ends. In either case the parent comment
            // is adjacent to the node; remove the comment from the parent and collect its metrics.
            aParentComment.accept(CommentVisitor.SINGLETON, pMetrics);
            if (aParentComment == aParentMainComment)
                aParent.setComment(null);
            else
                aParent.removeOrphanComment(aParentComment);

            if (aParentCommentRange.end.line <= aBeginLine)
                // If the parent comment precedes the node the latter now logically begins with the
                // parent comment.
                aBeginLine = aParentCommentRange.begin.line;
        }
    }


    /**
     * Collect an {@code Expression} by creating an {@code ExpressionStmt} from it and adding it
     * to a {@code StatementMetrics}.
     *
     * @param pExpression   The expression.
     * @param pMetrics      The metrics to add the created {@code ExpressionStmt} to.
     *
     * @throws NullPointerException if {@code pExpression} is non-null and {@code pMetrics} is null.
     */
    static void collectExpression(@CheckForNull Expression pExpression, @Nonnull StatementMetrics pMetrics)
    {
        if (pExpression != null)
            pMetrics.add(new ExpressionStmt(pExpression));
    }


    /**
     * Check if a node is an interface declaration.
     *
     * @param pNode The node to check, possibly null.
     *
     * @return  True if {@code pNode} is an interface declaration, false if not.
     */
    static boolean isInterface(@CheckForNull Node pNode)
    {
        return pNode instanceof ClassOrInterfaceDeclaration
               &&
               ((ClassOrInterfaceDeclaration) pNode).isInterface();
    }


    /**
     * Check if a node is an annotation declaration.
     *
     * @param pNode The node to check, possibly null.
     *
     * @return  True if {@code pNode} is an annotation declaration, false if not.
     */
    static boolean isAnnotation(@CheckForNull Node pNode)
    {
        return pNode instanceof AnnotationDeclaration;
    }


    /**
     * An abstract syntax tree visitor that visits comment nodes and collects metrics from them.
     * The collected metrics are added to the {@code CommentMetrics} passed as argument to each
     * {@code visit} method.
     *<p>
     * Instances of this class are immutable.
     */
    @Immutable
    static private class CommentVisitor extends VoidVisitorAdapter<CommentMetrics>
    {
        static final CommentVisitor SINGLETON = new CommentVisitor();

        @Override
        public void visit(@Nonnull BlockComment pComment, @Nonnull CommentMetrics pMetrics)
        {
            pMetrics.add(pComment);
        }

        @Override
        public void visit(@Nonnull JavadocComment pComment, @Nonnull CommentMetrics pMetrics)
        {
            pMetrics.add(pComment);
        }

        @Override
        public void visit(@Nonnull LineComment pComment, @Nonnull CommentMetrics pMetrics)
        {
            pMetrics.add(pComment);
        }
    }


    /**
     * Comparator that sorts comments on line position and then on column position.
     *<p>
     * Instances of this class are immutable.
     */
    @Immutable
    static private class CommentComparator implements Comparator<Comment>
    {
        static final CommentComparator SINGLETON = new CommentComparator();

        /**
         * Compare two comments base on their position within the enclosing compilation unit.
         *
         * @param pComment1 The first comment to be compared.
         * @param pComment2 The second comment to be compared.
         *
         * @return  A negative integer, zero, or a positive integer as the first comment is
         *          positioned before, equal to, or after the second comment.
         */
        @Override
        public int compare(@Nonnull Comment pComment1, @Nonnull Comment pComment2)
        {
            Range aRange1 = pComment1.getRange().orElse(NO_RANGE);
            Range aRange2 = pComment2.getRange().orElse(NO_RANGE);

            if (aRange1.begin.isBefore(aRange2.begin))
                return -1;
            else if (aRange1.begin.isAfter(aRange2.begin))
                return 1;

            if (aRange1.end.isBefore(aRange2.end))
                return -1;
            else if (aRange1.end.isAfter(aRange2.end))
                return 1;

            return 0;
        }
    }
}
