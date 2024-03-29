/*
 * Copyright 2016, 2018, 2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.util.function.Function;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.PackageMetrics;
import static org.myire.scent.collect.Collectors.collectAdjacentParentComments;
import static org.myire.scent.collect.Collectors.collectNodeComments;


/**
 * A collector of source code metrics for compilation units and their types.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class CompilationUnitMetricsCollector
{
    private final CompilationUnit fCompilationUnitNode;
    private final String fCompilationUnitName;


    /**
     * Create a new {@code CompilationUnitMetricsCollector}.
     *
     * @param pCompilationUnitNode  The compilation unit node to collect metrics for.
     * @param pCompilationUnitName  The name of the compilation unit.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    CompilationUnitMetricsCollector(
            @Nonnull CompilationUnit pCompilationUnitNode,
            @Nonnull String pCompilationUnitName)
    {
        fCompilationUnitNode = requireNonNull(pCompilationUnitNode);
        fCompilationUnitName = requireNonNull(pCompilationUnitName);
    }


    /**
     * Collect metrics for the compilation unit node passed to the constructor and add them to a
     * {@code PackageMetrics} instance.
     *
     * @param pPackageMetricsLookup A function that returns the {@code PackageMetrics} for a
     *                              {@code PackageDeclaration}.
     *
     * @throws NullPointerException if {@code pPackageMetricsLookup} is null.
     */
    void collect(@Nonnull Function<PackageDeclaration, PackageMetrics> pPackageMetricsLookup)
    {
        CompilationUnitMetrics aMetrics = new CompilationUnitMetrics(fCompilationUnitName);

        // Lookup the package metrics to add the collected metrics to.
        PackageDeclaration aPackageDeclaration = fCompilationUnitNode.getPackageDeclaration().orElse(null);
        PackageMetrics aPackageMetrics = pPackageMetricsLookup.apply(aPackageDeclaration);

        // Let each type declaration accept the visitor to collect metrics for the corresponding
        // top-level type.
        for (TypeDeclaration aType : fCompilationUnitNode.getTypes())
            aType.accept(TypeVisitor.SINGLETON, aMetrics);

        // Collect all comments from the import declarations and associate them with the compilation
        // unit, since import declarations don't have their own metrics.
        for (ImportDeclaration aImport : fCompilationUnitNode.getImports())
            collectNodeComments(aImport, aMetrics.getComments());

        // Collect all comments from the package declaration if there is one.
        if (aPackageDeclaration != null)
        {
            // If there are no types in the compilation unit the package declaration comments are
            // associated with the package declaration, otherwise they are associated with the
            // compilation unit, since a package declaration isn't viewed as a separate code element
            // unless it is the only entity in the compilation unit.
            CommentMetrics aPackageCommentMetrics =
                fCompilationUnitNode.getTypes().isEmpty() ? aPackageMetrics.getComments() : aMetrics.getComments();
            collectAdjacentParentComments(aPackageDeclaration, aPackageCommentMetrics, false);
            collectNodeComments(aPackageDeclaration, aPackageCommentMetrics);
        }

        // Collect comments for the compilation unit.
        collectNodeComments(fCompilationUnitNode, aMetrics.getComments());

        aPackageMetrics.add(aMetrics);
    }


    /**
     * An abstract syntax tree visitor that visits type nodes and collects source code metrics from
     * them. The collected type metrics are added to the {@code CompilationUnitMetrics} passed as
     * argument to each {@code visit} method.
     *<p>
     * Instances of this class are immutable.
     */
    @Immutable
    static private class TypeVisitor extends VoidVisitorAdapter<CompilationUnitMetrics>
    {
        static final TypeVisitor SINGLETON = new TypeVisitor();

        @Override
        public void visit(@Nonnull ClassOrInterfaceDeclaration pType, @Nonnull CompilationUnitMetrics pMetrics)
        {
            pMetrics.add(new TypeMetricsCollector(pType).collect());
        }

        @Override
        public void visit(@Nonnull EnumDeclaration pEnum, @Nonnull CompilationUnitMetrics pMetrics)
        {
            pMetrics.add(new TypeMetricsCollector(pEnum).collect());
        }

        @Override
        public void visit(@Nonnull AnnotationDeclaration pAnnotation, @Nonnull CompilationUnitMetrics pMetrics)
        {
            pMetrics.add(new TypeMetricsCollector(pAnnotation).collect());
        }

        @Override
        public void visit(@Nonnull RecordDeclaration pRecord,  @Nonnull CompilationUnitMetrics pMetrics)
        {
            pMetrics.add(new TypeMetricsCollector(pRecord).collect());
        }
    }
}
