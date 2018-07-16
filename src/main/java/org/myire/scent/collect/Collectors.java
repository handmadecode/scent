/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.github.javaparser.ast.DocumentableNode;
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
    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private Collectors()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Move the comments from one node to another. The main comment and JavaDoc comment (if
     * applicable) will not be moved if they are non-null in the target node.
     *
     * @param pSourceNode   The node to move the comments from.
     * @param pTargetNode   The node to move the comments to.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void moveNodeComments(@Nonnull Node pSourceNode, @Nonnull Node pTargetNode)
    {
        // Move the main comment if not present in the target node.
        if (pTargetNode.getComment() == null)
        {
            pTargetNode.setComment(pSourceNode.getComment());
            pSourceNode.setComment(null);
        }

        // Move any orphan comments.
        pTargetNode.getOrphanComments().addAll(pSourceNode.getOrphanComments());
        pSourceNode.getOrphanComments().clear();

        // Move the JavaDoc comment if applicable and not present in the target node.
        if (pSourceNode instanceof DocumentableNode && pTargetNode instanceof DocumentableNode)
        {
            DocumentableNode aDocumentableSource = (DocumentableNode) pSourceNode;
            DocumentableNode aDocumentableTarget = (DocumentableNode) pTargetNode;
            if (aDocumentableTarget.getJavaDoc() == null)
            {
                aDocumentableTarget.setJavaDoc(aDocumentableSource.getJavaDoc());
                aDocumentableSource.setJavaDoc(null);
            }
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
        CommentVisitor.SINGLETON.maybeVisit(pNode.getComment(), pMetrics);
        pNode.setComment(null);
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
        Iterator<Comment> aOrphans = pNode.getOrphanComments().iterator();
        while (aOrphans.hasNext())
        {
            CommentVisitor.SINGLETON.maybeVisit(aOrphans.next(), pMetrics);
            aOrphans.remove();
        }

        // Collect and remove  the JavaDoc comment if applicable.
        if (pNode instanceof DocumentableNode)
        {
            CommentVisitor.SINGLETON.maybeVisit(((DocumentableNode) pNode).getJavaDoc(), pMetrics);
            ((DocumentableNode) pNode).setJavaDoc(null);
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
        for (Node aChild : pNode.getChildrenNodes())
        {
            collectNodeComments(aChild, pMetrics);
            collectChildComments(aChild, pMetrics);
        }
    }


    /**
     * Collect the orphan comments from a node's parent that logically belong to the node, not to
     * the parent. An orphan comment in the parent that immediately precedes the node's comment
     * belongs to the node rather than to the parent. The same goes for an orphan comment that
     * begins on the same line as the node ends on.
     *<p>
     * The collected comments will be removed from the parent to prevent them from being collected
     * twice when the parent's comments are collected in a call to
     * {@link #collectNodeComments(Node, CommentMetrics)}.
     *
     * @param pNode     The node to collect parent orphan comments for.
     * @param pMetrics  Where to put the collected metrics.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static void collectParentOrphanComments(@Nonnull Node pNode, @Nonnull CommentMetrics pMetrics)
    {
        Node aParent = pNode.getParentNode();
        if (aParent == null)
            // If the node hasn't got a parent there are no orphan comments to collect.
            return;

        // Get the begin and end line of the node for which comment metrics are collected.
        int aBeginLine = pNode.getBeginLine();
        int aEndLine = pNode.getEndLine();

        // Adjust the begin line to include any comment on the node.
        Comment aComment = pNode.getComment();
        if (aComment != null)
            aBeginLine = aComment.getBeginLine();

        // Sort the parent's orphan comments on their position in the enclosing compilation unit.
        List<Comment> aOrphans = aParent.getOrphanComments();
        Collections.sort(aOrphans, CommentComparator.SINGLETON);

        // Examine the parent's orphan comments starting from the bottom of the enclosing
        // compilation unit.
        ListIterator<Comment> aIterator = aOrphans.listIterator(aOrphans.size());
        while (aIterator.hasPrevious())
        {
            Comment aOrphan = aIterator.previous();
            if (aOrphan.getEndLine() < aBeginLine - 1)
                // When an orphan comment that ends on a line not immediately preceding the node
                // being examined there are no more orphans adjacent to that node.
                break;

            if (aOrphan.getBeginLine() > aEndLine)
                // This orphan comments begins after the node being examined, continue with the
                // previous orphan, which is located above the current.
                continue;

            // The orphan comment either ends on the same line or the line immediately preceding the
            // node, or begins on the same line as the node ends. In either case the orphan
            // logically belongs to the node rather than to its parent; remove the comment from the
            // parent and collect its metrics.
            aIterator.remove();
            aOrphan.accept(CommentVisitor.SINGLETON, pMetrics);

            if (aOrphan.getEndLine() <= aBeginLine)
                // If the orphan precedes the node the latter now logically begins with the orphan.
                aBeginLine = aOrphan.getBeginLine();
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
        {
            ExpressionStmt aStatement =
                    new ExpressionStmt(
                            pExpression.getBeginLine(),
                            pExpression.getBeginColumn(),
                            pExpression.getEndLine(),
                            pExpression.getEndColumn(),
                            pExpression);

            pMetrics.add(aStatement);
        }
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

        /**
         * Visit a {@code Comment} node if it is non-null by calling its {@code accept} method.
         *
         * @param pComment  The node to invoke the {@code accept} method on if it is non-null.
         * @param pMetrics  The argument to pass to the {@code accept} method.
         *
         * @throws NullPointerException if {@code pNode} is non-null and {@code pMetrics} is null.
         */
        void maybeVisit(@CheckForNull Comment pComment, @Nonnull CommentMetrics pMetrics)
        {
            if (pComment != null)
                pComment.accept(this, pMetrics);
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
            int aResult = pComment1.getBeginLine() - pComment2.getBeginLine();
            if (aResult != 0)
                return aResult;

            aResult = pComment1.getEndLine() - pComment2.getEndLine();
            if (aResult != 0)
                return aResult;

            return pComment1.getBeginColumn() - pComment2.getBeginColumn();
        }
    }
}
