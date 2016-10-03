/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

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
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.CompilationUnitMetrics;
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
     * Collect metrics for the compilation unit node passed to the constructor.
     *
     * @return  A new {@code CompilationUnitMetrics} instance with the collected metrics, never
     *          null.
     */
    @Nonnull
    CompilationUnitMetrics collect()
    {
        CompilationUnitMetrics aMetrics = new CompilationUnitMetrics(fCompilationUnitName);

        // Let each type declaration accept the visitor to collect metrics for the corresponding
        // top-level type.
        for (TypeDeclaration aType : fCompilationUnitNode.getTypes())
            aType.accept(TypeVisitor.SINGLETON, aMetrics);

        // Collect comments for the compilation unit.
        collectNodeComments(fCompilationUnitNode, aMetrics.getComments());

        // Collect all comments from the package declaration and associate them with the compilation
        // unit, since package declarations don't have their own metrics.
        PackageDeclaration aPackage = fCompilationUnitNode.getPackage();
        if (aPackage != null)
            collectNodeComments(aPackage, aMetrics.getComments());

        // Collect all comments from the import declarations and associate them with the compilation
        // unit, since import declarations don't have their own metrics.
        for (ImportDeclaration aImport : fCompilationUnitNode.getImports())
            collectNodeComments(aImport, aMetrics.getComments());

        return aMetrics;
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
    }
}
