/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.MethodMetrics;
import static org.myire.scent.collect.Collectors.collectAdjacentParentOrphanComments;
import static org.myire.scent.collect.Collectors.collectChildComments;
import static org.myire.scent.collect.Collectors.collectExpression;
import static org.myire.scent.collect.Collectors.collectNodeComments;
import static org.myire.scent.collect.Collectors.moveNodeComments;


/**
 * A collector of source code metrics for methods and their contents.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class MethodMetricsCollector
{
    private final Node fMethodNode;
    private final String fMethodName;
    private final MethodMetrics.Kind fMethodKind;


    /**
     * Create a new {@code MethodMetricsCollector} for an initializer.
     *
     * @param pInitializer  The initializer to collect metrics for.
     *
     * @throws NullPointerException if {@code pInitializer} is null.
     */
    @Nonnull
    MethodMetricsCollector(@Nonnull InitializerDeclaration pInitializer)
    {
        fMethodNode = pInitializer;
        if (pInitializer.isStatic())
        {
            fMethodName = "clinit";
            fMethodKind = MethodMetrics.Kind.STATIC_INITIALIZER;
        }
        else
        {
            fMethodName = "init";
            fMethodKind = MethodMetrics.Kind.INSTANCE_INITIALIZER;
        }
    }


    /**
     * Create a new {@code MethodMetricsCollector} for a constructor.
     *
     * @param pConstructor  The constructor to collect metrics for.
     *
     * @throws NullPointerException if {@code pConstructor} is null.
     */
    @Nonnull
    MethodMetricsCollector(@Nonnull ConstructorDeclaration pConstructor)
    {
        fMethodNode = pConstructor;
        fMethodName = pConstructor.getDeclarationAsString(false, false, false);
        fMethodKind = MethodMetrics.Kind.CONSTRUCTOR;
    }


    /**
     * Create a new {@code MethodMetricsCollector} for a method.
     *
     * @param pMethod   The method to collect metrics for.
     *
     * @throws NullPointerException if {@code pMethod} is null.
     */
    @Nonnull
    MethodMetricsCollector(@Nonnull MethodDeclaration pMethod)
    {
        fMethodNode = pMethod;
        fMethodName = pMethod.getDeclarationAsString(false, false, false);
        fMethodKind = getMethodKind(pMethod);
    }


    /**
     * Collect metrics for the method node passed to the constructor.
     *
     * @return  A new {@code MethodMetrics} with the collected metrics, never null.
     */
    @Nonnull
    MethodMetrics collect()
    {
        MethodMetrics aMetrics = new MethodMetrics(fMethodName, fMethodKind);

        // Collect the method's statements and local types.
        fMethodNode.accept(MethodMemberVisitor.SINGLETON, aMetrics);

        // Collect the method's comments, including the orphan comments of the enclosing type that
        // logically belong to the method's comment.
        collectAdjacentParentOrphanComments(fMethodNode, aMetrics.getComments());
        collectNodeComments(fMethodNode, aMetrics.getComments());

        // Collect all comments not already collected from the method's children, e.g. comments
        // associated with a BlockStmt (which doesn't count as a statement).
        collectChildComments(fMethodNode, aMetrics.getComments());

        return aMetrics;
    }


    /**
     * Get the appropriate {@code MethodMetrics.Kind} for a method declaration.
     *
     * @param pMethod   The method declaration.
     *
     * @return  The kind of method, never null.
     *
     * @throws NullPointerException if {@code pMethod} is null.
     */
    @Nonnull
    static private MethodMetrics.Kind getMethodKind(@Nonnull MethodDeclaration pMethod)
    {
        if (pMethod.isDefault())
            return MethodMetrics.Kind.DEFAULT_METHOD;
        else if (pMethod.getModifiers().contains(Modifier.STATIC))
            return MethodMetrics.Kind.STATIC_METHOD;
        else if (pMethod.getModifiers().contains(Modifier.ABSTRACT))
            return MethodMetrics.Kind.ABSTRACT_METHOD;
        else if (pMethod.getModifiers().contains(Modifier.NATIVE))
            return MethodMetrics.Kind.NATIVE_METHOD;
        else if (Collectors.isInterface(pMethod.getParentNode().orElse(null)) && !pMethod.getBody().isPresent())
            // Only interface methods without a body are abstract.
            return MethodMetrics.Kind.ABSTRACT_METHOD;
        else
            return MethodMetrics.Kind.INSTANCE_METHOD;
    }


    /**
     * An abstract syntax tree visitor that visits method members and collects metrics for them. The
     * collected metrics are added to the {@code MethodMetrics} passed as  argument to each
     * {@code visit} method.
     *<p>
     * Note that not all subclasses of {@code com.github.javaparser.ast.stmt.Statement} are
     * collected; those that don't count as statements are ignored, e.g {@code BlockStmt} and
     * {@code EmptyStmt}.
     *<p>
     * Instances of this class are immutable.
     */
    @Immutable
    static private class MethodMemberVisitor extends VoidVisitorAdapter<MethodMetrics>
    {
        static final MethodMemberVisitor SINGLETON = new MethodMemberVisitor();

        @Override
        public void visit(@Nonnull AssertStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull BreakStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ContinueStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull DoStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ExplicitConstructorInvocationStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ExpressionStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ForeachStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ForStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull IfStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull LocalClassDeclarationStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            // Move any comments from the statement to the local class declaration so the
            // TypeMetricsCollector can pick them up. The statement itself is not collected, only
            // the local class declaration.
            moveNodeComments(pStatement, pStatement.getClassDeclaration());
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ReturnStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull SwitchEntryStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull SwitchStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull SynchronizedStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ThrowStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull TryStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            // Add the try statement.
            pMetrics.getStatements().add(pStatement);

            // Add any variable initializations in try-with statements.
            for (Expression aResource : pStatement.getResources())
            {
                if (aResource.isVariableDeclarationExpr())
                {
                    for (VariableDeclarator aDeclarator : aResource.asVariableDeclarationExpr().getVariables())
                        aDeclarator.getInitializer().ifPresent(i -> collectExpression(i, pMetrics.getStatements()));
                }
            }

            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull WhileStmt pStatement, @Nonnull MethodMetrics pMetrics)
        {
            collectStatement(pStatement, pMetrics);
            super.visit(pStatement, pMetrics);
        }

        @Override
        public void visit(@Nonnull ClassOrInterfaceDeclaration pType, @Nonnull MethodMetrics pMetrics)
        {
            // Local class, collect as type metrics.
            pMetrics.add(new TypeMetricsCollector(pType).collect());
        }

        @Override
        public void visit(@Nonnull ObjectCreationExpr pExpression, @Nonnull MethodMetrics pMetrics)
        {
            if (pExpression.getAnonymousClassBody().isPresent())
                // Anonymous class, collect as type metrics.
                pMetrics.add(new TypeMetricsCollector(pExpression).collect());
        }

        /**
         * Collect metrics for a statement and its comments.
         *
         * @param pStatement    The statement.
         * @param pMetrics      Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        static private void collectStatement(@Nonnull Statement pStatement, @Nonnull MethodMetrics pMetrics)
        {
            pMetrics.getStatements().add(pStatement);
            collectNodeComments(pStatement, pMetrics.getComments());
        }
    }
}
