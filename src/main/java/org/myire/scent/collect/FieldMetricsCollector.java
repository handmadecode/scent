/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.FieldMetrics;
import static org.myire.scent.collect.Collectors.collectAdjacentParentOrphanComments;
import static org.myire.scent.collect.Collectors.collectExpression;
import static org.myire.scent.collect.Collectors.collectNodeComment;
import static org.myire.scent.collect.Collectors.collectNodeComments;


/**
 * A collector of source code metrics for fields and their contents.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class FieldMetricsCollector
{
    private final Node fFieldNode;
    private final String fFieldName;
    private final FieldMetrics.Kind fFieldKind;
    private final Expression fInitializerExpression;
    private final CommentMetrics fParentComments = new CommentMetrics();


    /**
     * Create a new {@code FieldMetricsCollector} for an annotation member or, more specifically, an
     * annotation type element.
     *
     * @param pAnnotationMember The annotation member to collect metrics for.
     *
     * @throws NullPointerException if {@code pAnnotationMember} is null.
     */
    @Nonnull
    FieldMetricsCollector(@Nonnull AnnotationMemberDeclaration pAnnotationMember)
    {
        fFieldNode = pAnnotationMember;
        fFieldName = pAnnotationMember.getName().getIdentifier();
        fFieldKind = FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT;
        fInitializerExpression = pAnnotationMember.getDefaultValue().orElse(null);

        // Collect any orphan comments from the parent that should be associated with the annotation
        // type element. This could be done in collect(), but is done here to be symmetric with the
        // constructor that creates an instance from a FieldDeclaration, where the parent comments
        // must be collected in the constructor.
        collectAdjacentParentOrphanComments(pAnnotationMember, fParentComments);
    }


    /**
     * Create a new {@code FieldMetricsCollector} for an enum constant.
     *
     * @param pEnumConstant The enum constant to collect metrics for.
     *
     * @throws NullPointerException if {@code pEnumConstant} is null.
     */
    @Nonnull
    FieldMetricsCollector(@Nonnull EnumConstantDeclaration pEnumConstant)
    {
        fFieldNode = pEnumConstant;
        fFieldName = pEnumConstant.getName().getIdentifier();
        fFieldKind = FieldMetrics.Kind.ENUM_CONSTANT;
        fInitializerExpression = null;

        // Collect any orphan comments from the parent that should be associated with the enum
        // constant.
        collectAdjacentParentOrphanComments(pEnumConstant, fParentComments);
    }


    /**
     * Create a new {@code FieldMetricsCollector} for a field.
     *
     * @param pField    The field declaration.
     * @param pVariable The variable declarator within the field declaration that holds the field.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    @Nonnull
    FieldMetricsCollector(@Nonnull FieldDeclaration pField, @Nonnull VariableDeclarator pVariable)
    {
        fFieldNode = pVariable;
        fFieldName = pVariable.getName().getIdentifier();
        fFieldKind = getFieldKind(pField);
        fInitializerExpression = pVariable.getInitializer().orElse(null);

        // Collect any comments from the declaration (including any of its parent's orphan comments
        // that should be associated with it). These comments should be associated with the
        // variable declarator instead, since the declaration itself doesn't have any metrics. By
        // collecting (and removing) these comments here rather than in collect(), only the first
        // variable declarator gets the field's comment(s).
        collectAdjacentParentOrphanComments(pField, fParentComments);
        collectNodeComment(pField, fParentComments);

        // Collect any orphan comments from the variable declarator's parent (i.e. the field
        // declaration ) that should be associated with the variable declarator.
        collectAdjacentParentOrphanComments(pVariable, fParentComments);
    }


    /**
     * Collect metrics for the field node passed to the constructor.
     *
     * @return  A new {@code FieldMetrics} with the collected metrics, never null.
     */
    @Nonnull
    FieldMetrics collect()
    {
        FieldMetrics pMetrics = new FieldMetrics(fFieldName, fFieldKind);

        // Collect any initializer statement for the field.
        collectExpression(fInitializerExpression, pMetrics.getStatements());

        // Collect the field's comments and add the comments already collected from the parent node.
        collectNodeComments(fFieldNode, pMetrics.getComments());
        pMetrics.getComments().add(fParentComments);

        return pMetrics;
    }


    /**
     * Get the appropriate {@code FieldMetrics.Kind} for a field declaration.
     *
     * @param pField    The field declaration.
     *
     * @return  The kind of field, never null.
     *
     * @throws NullPointerException if {@code pField} is null.
     */
    @Nonnull
    static private FieldMetrics.Kind getFieldKind(@Nonnull FieldDeclaration pField)
    {
        // Fields in interfaces and annotations are implicitly static.
        boolean aIsStatic =
                pField.getModifiers().contains(Modifier.STATIC)
                ||
                Collectors.isInterface(pField.getParentNode().orElse(null))
                ||
                Collectors.isAnnotation(pField.getParentNode().orElse(null));

        return aIsStatic ? FieldMetrics.Kind.STATIC_FIELD : FieldMetrics.Kind.INSTANCE_FIELD;
    }
}
