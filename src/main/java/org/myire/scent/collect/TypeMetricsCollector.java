/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.TypeMetrics;
import static org.myire.scent.collect.Collectors.collectNodeComments;
import static org.myire.scent.collect.Collectors.collectParentOrphanComments;


/**
 * A collector of source code metrics for types and their members.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class TypeMetricsCollector
{
    private final Node fTypeNode;
    private final String fTypeName;
    private final TypeMetrics.Kind fTypeKind;


    /**
     * Create a new {@code TypeMetricsCollector} for a class or interface.
     *
     * @param pType The class or interface to collect metrics for.
     *
     * @throws NullPointerException if {@code pType} is null.
     */
    TypeMetricsCollector(@Nonnull ClassOrInterfaceDeclaration pType)
    {
        fTypeNode = pType;
        fTypeName = pType.getName();
        fTypeKind = pType.isInterface() ? TypeMetrics.Kind.INTERFACE : TypeMetrics.Kind.CLASS;
    }


    /**
     * Create a new {@code TypeMetricsCollector} for an enum.
     *
     * @param pEnum The enum to collect metrics for.
     *
     * @throws NullPointerException if {@code pEnum} is null.
     */
    TypeMetricsCollector(@Nonnull EnumDeclaration pEnum)
    {
        fTypeNode = pEnum;
        fTypeName = pEnum.getName();
        fTypeKind = TypeMetrics.Kind.ENUM;
    }


    /**
     * Create a new {@code TypeMetricsCollector} for an enum constant with class body.
     *
     * @param pEnumConstant The enum constant to collect metrics for.
     *
     * @throws NullPointerException if {@code pEnumConstant} is null.
     */
    TypeMetricsCollector(@Nonnull EnumConstantDeclaration pEnumConstant)
    {
        fTypeNode = pEnumConstant;
        fTypeName = pEnumConstant.getName();
        fTypeKind = TypeMetrics.Kind.ENUM_CONSTANT;
    }


    /**
     * Create a new {@code TypeMetricsCollector} for an annotation.
     *
     * @param pAnnotation The annotation to collect metrics for.
     *
     * @throws NullPointerException if {@code pAnnotation} is null.
     */
    TypeMetricsCollector(@Nonnull AnnotationDeclaration pAnnotation)
    {
        fTypeNode = pAnnotation;
        fTypeName = pAnnotation.getName();
        fTypeKind = TypeMetrics.Kind.ANNOTATION;
    }


    /**
     * Create a new {@code TypeMetricsCollector} for an anonymous class.
     *
     * @param pExpression   The expression where the anonymous class is created/declared.
     *
     * @throws NullPointerException if {@code pAnnotation} is null.
     */
    TypeMetricsCollector(@Nonnull ObjectCreationExpr pExpression)
    {
        fTypeNode = pExpression;
        fTypeName = "Anonymous$" + pExpression.getType().getName();
        fTypeKind = TypeMetrics.Kind.ANONYMOUS_CLASS;
    }


    /**
     * Collect metrics for the type node passed to the constructor.
     *
     * @return  A new {@code TypeMetrics} with the collected metrics, never null.
     */
    @Nonnull
    TypeMetrics collect()
    {
        TypeMetrics aMetrics = new TypeMetrics(fTypeName, fTypeKind);

        // Collect metrics from the type's members.
        new TypeMemberVisitor(fTypeNode).visitStartNode(aMetrics);

        // Collect the type's comments, including the orphan comments of the enclosing compilation
        // unit that logically belong to the type's comment.
        collectParentOrphanComments(fTypeNode, aMetrics.getComments());
        collectNodeComments(fTypeNode, aMetrics.getComments());

        return aMetrics;
    }


    /**
     * An abstract syntax tree visitor that visits type member nodes and collects metrics from them.
     * The collected metrics are added to the {@code TypeMetrics} passed as argument to each
     * {@code visit} method.
     *<p>
     * Instances of this class are <b>not</b> safe for use by multiple threads without external
     * synchronization.
     */
    @NotThreadSafe
    static private class TypeMemberVisitor extends VoidVisitorAdapter<TypeMetrics>
    {
        private final Node fStartNode;

        /**
         * Create a new {@code TypeMemberVisitor}.
         *
         * @param pStartNode    The node where the visitor starts traversing the abstract syntax
         *                      tree. Metrics will not be collected for this node, as it isn't a
         *                      member of itself.
         *
         * @throws NullPointerException if {@code pStartNode} is null.
         */
        TypeMemberVisitor(@Nonnull Node pStartNode)
        {
            fStartNode = requireNonNull(pStartNode);
        }

        /**
         * Collect metrics for a class or interface and its fields, methods, and inner types.
         *
         * @param pType     The class or interface to collect metrics for.
         * @param pMetrics  Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull ClassOrInterfaceDeclaration pType, @Nonnull TypeMetrics pMetrics)
        {
            if (isStartNode(pType))
                // The start node, don't collect any metrics, only continue traversing the tree.
                super.visit(pType, pMetrics);
            else
                // Inner class/interface, collect metrics.
                pMetrics.add(new TypeMetricsCollector(pType).collect());
        }

        /**
         * Collect metrics for an enum and its fields, methods, and inner types.
         *
         * @param pEnum     The enum to collect metrics for.
         * @param pMetrics  Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull EnumDeclaration pEnum, @Nonnull TypeMetrics pMetrics)
        {
            if (isStartNode(pEnum))
                // The start node, don't collect any metrics, only continue traversing the tree.
                super.visit(pEnum, pMetrics);
            else
                // Inner enum, collect metrics.
                pMetrics.add(new TypeMetricsCollector(pEnum).collect());
        }

        /**
         * Collect metrics for an annotation and its fields, methods, and inner types.
         *
         * @param pAnnotation   The annotation to collect metrics for.
         * @param pMetrics      Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull AnnotationDeclaration pAnnotation, @Nonnull TypeMetrics pMetrics)
        {
            if (isStartNode(pAnnotation))
                // The start node, don't collect any metrics, only continue traversing the tree.
                super.visit(pAnnotation, pMetrics);
            else
                // Inner annotation, collect metrics.
                pMetrics.add(new TypeMetricsCollector(pAnnotation).collect());
        }

        /**
         * Collect metrics for a static or instance initializer and its statements and comments.
         *
         * @param pInitializer  The initializer to collect metrics for.
         * @param pMetrics      Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull InitializerDeclaration pInitializer, @Nonnull TypeMetrics pMetrics)
        {
            pMetrics.add(new MethodMetricsCollector(pInitializer).collect());
        }

        /**
         * Collect metrics for a constructor and its statements and comments.
         *
         * @param pConstructor  The constructor to collect metrics for.
         * @param pMetrics      Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull ConstructorDeclaration pConstructor, @Nonnull TypeMetrics pMetrics)
        {
            pMetrics.add(new MethodMetricsCollector(pConstructor).collect());
        }

        /**
         * Collect metrics for a static, instance, or default method and its statements and
         * comments.
         *
         * @param pMethod   The method to collect metrics for.
         * @param pMetrics  Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull MethodDeclaration pMethod, @Nonnull TypeMetrics pMetrics)
        {
            pMetrics.add(new MethodMetricsCollector(pMethod).collect());
        }

        /**
         * Collect metrics for an annotation member and its statements and comments.
         *
         * @param pAnnotationMember The annotation member to collect metrics for.
         * @param pMetrics          Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull AnnotationMemberDeclaration pAnnotationMember, @Nonnull TypeMetrics pMetrics)
        {
            pMetrics.add(new FieldMetricsCollector(pAnnotationMember).collect());
        }

        /**
         * Collect metrics for an enum constant. Enum constants without a class body are collected
         * as field metrics, whereas enum constants with a class body are collected as inner type
         * metrics.
         *
         * @param pEnumConstant The enum constant to collect metrics for.
         * @param pMetrics      Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull EnumConstantDeclaration pEnumConstant, @Nonnull TypeMetrics pMetrics)
        {
            if (isStartNode(pEnumConstant))
                // The start node, don't collect any metrics, only continue traversing the tree.
                super.visit(pEnumConstant, pMetrics);
            else if (pEnumConstant.getClassBody().isEmpty())
                // The enum constant does not have a class body, collect as field metrics.
                pMetrics.add(new FieldMetricsCollector(pEnumConstant).collect());
            else
                // The enum constant has a class body, collect as inner type metrics.
                pMetrics.add(new TypeMetricsCollector(pEnumConstant).collect());
        }

        /**
         * Collect metrics for a field and and its statements and comments.
         *
         * @param pField    The field to collect metrics for.
         * @param pMetrics  Where to add the collected metrics.
         *
         * @throws NullPointerException if any of the parameters is null.
         */
        @Override
        public void visit(@Nonnull FieldDeclaration pField, @Nonnull TypeMetrics pMetrics)
        {
            // A field declaration may contain multiple variable declarators, and each of them
            // should be collected as a field with the same kind.
            for (VariableDeclarator aVariable : pField.getVariables())
                pMetrics.add(new FieldMetricsCollector(pField, aVariable).collect());
        }

        /**
         * Visit the start node and collect metrics for it.
         *
         * @param pMetrics  Where to put the collected metrics.
         *
         * @throws NullPointerException if {@code pMetrics} is null.
         */
        void visitStartNode(@Nonnull TypeMetrics pMetrics)
        {
            fStartNode.accept(this, pMetrics);
        }

        /**
         * Check if a node is the one where this visitor starts traversing the syntax tree.
         *
         * @param pNode The node to check.
         *
         * @return  True if {@code pNode} is the same node as was passed to the constructor, false
         *          if not.
         */
        private boolean isStartNode(@Nullable Node pNode)
        {
            return fStartNode == pNode;
        }
    }
}
