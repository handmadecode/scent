/*
 * Copyright 2018-2019 Peter Franzen. All rights reserved.
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
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.myire.scent.metrics.ModularCompilationUnitMetrics;
import org.myire.scent.metrics.ModuleDeclarationMetrics;
import static org.myire.scent.collect.Collectors.collectAdjacentParentComments;
import static org.myire.scent.collect.Collectors.collectNodeComments;


/**
 * A collector of source code metrics for modular compilation units.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class ModularCompilationUnitMetricsCollector
{
    private final CompilationUnit fCompilationUnitNode;
    private final String fCompilationUnitName;
    private final ModuleDeclaration fModuleDeclarationNode;


    /**
     * Create a new {@code ModularCompilationUnitMetricsCollector}.
     *
     * @param pCompilationUnitNode  The compilation unit node to collect metrics for.
     * @param pCompilationUnitName  The name of the compilation unit.
     *
     * @throws NullPointerException if any of the parameters is null.
     * @throws IllegalArgumentException if {@code pCompilationUnitNode} does not contain a module
     *                                  declaration.
     */
    ModularCompilationUnitMetricsCollector(
        @Nonnull CompilationUnit pCompilationUnitNode,
        @Nonnull String pCompilationUnitName)
    {
        fCompilationUnitNode = requireNonNull(pCompilationUnitNode);
        fCompilationUnitName = requireNonNull(pCompilationUnitName);

        fModuleDeclarationNode =
            fCompilationUnitNode.getModule().orElseThrow(
                () -> new IllegalArgumentException("Compilation unit does not contain module declaration")
            );
    }


    /**
     * Collect metrics for the compilation unit node passed to the constructor.
     *
     * @return  A new {@code ModularCompilationUnitMetrics} with the collected metrics, never null.
     */
    @Nonnull
    ModularCompilationUnitMetrics collect()
    {
        // Collect metrics for the module declaration.
        ModuleDeclarationMetrics aModuleMetrics = collectModuleMetrics();

        ModularCompilationUnitMetrics aMetrics =
            new ModularCompilationUnitMetrics(fCompilationUnitName, aModuleMetrics);

        // Collect all comments from the import declarations and associate them with the compilation
        // unit, since import declarations don't have their own metrics.
        for (ImportDeclaration aImport : fCompilationUnitNode.getImports())
            collectNodeComments(aImport, aMetrics.getComments());

        // Collect any comments belonging to the compilation unit, e.g. file header and/or footer.
        collectNodeComments(fCompilationUnitNode, aMetrics.getComments());

        return aMetrics;
    }


    /**
     * Collect metrics for the module declaration enclosed in the compilation unit passed to the
     * constructor.
     *
     * @return  A new {@code ModuleDeclarationMetrics} with the collected metrics, never null.
     */
    @Nonnull
    private ModuleDeclarationMetrics collectModuleMetrics()
    {
        ModuleDeclarationMetrics aMetrics =
            new ModuleDeclarationMetrics(
                fModuleDeclarationNode.getName().asString(),
                fModuleDeclarationNode.isOpen());

        // Collect metrics from the module declaration's members.
        fModuleDeclarationNode.accept(ModuleMemberVisitor.SINGLETON, aMetrics);

        // Collect the module declaration's comments, including the adjacent comments of the
        // enclosing compilation unit that logically are comments of the module declaration.
        collectAdjacentParentComments(fModuleDeclarationNode, aMetrics.getComments(), false);
        collectNodeComments(fModuleDeclarationNode, aMetrics.getComments());

        return aMetrics;
    }


    /**
     * An abstract syntax tree visitor that visits module declaration members and collects metrics
     * for them. The collected metrics are added to the {@code ModuleDeclarationMetrics} passed as
     * argument to each {@code visit} method.
     *<p>
     * Instances of this class are immutable.
     */
    @Immutable
    static private class ModuleMemberVisitor extends VoidVisitorAdapter<ModuleDeclarationMetrics>
    {
        static final ModuleMemberVisitor SINGLETON = new ModuleMemberVisitor();


        @Override
        public void visit(
            @Nonnull ModuleRequiresDirective pRequiresDirective,
            @Nonnull ModuleDeclarationMetrics pMetrics)
        {
            pMetrics.add(pRequiresDirective);
            collectNodeComments(pRequiresDirective, pMetrics.getComments());
            super.visit(pRequiresDirective, pMetrics);
        }


        @Override
        public void visit(
            @Nonnull ModuleExportsDirective pExportsDirective,
            @Nonnull ModuleDeclarationMetrics pMetrics)
        {
            pMetrics.add(pExportsDirective);
            collectNodeComments(pExportsDirective, pMetrics.getComments());
            super.visit(pExportsDirective, pMetrics);
        }


        @Override
        public void visit(
            @Nonnull ModuleProvidesDirective pProvidesDirective,
            @Nonnull ModuleDeclarationMetrics pMetrics)
        {
            pMetrics.add(pProvidesDirective);
            collectNodeComments(pProvidesDirective, pMetrics.getComments());
            super.visit(pProvidesDirective, pMetrics);
        }


        @Override
        public void visit(
            @Nonnull ModuleUsesDirective pUsesDirective,
            @Nonnull ModuleDeclarationMetrics pMetrics)
        {
            pMetrics.add(pUsesDirective);
            collectNodeComments(pUsesDirective, pMetrics.getComments());
            super.visit(pUsesDirective, pMetrics);
        }


        @Override
        public void visit(
            @Nonnull ModuleOpensDirective pOpensDirective,
            @Nonnull ModuleDeclarationMetrics pMetrics)
        {
            pMetrics.add(pOpensDirective);
            collectNodeComments(pOpensDirective, pMetrics.getComments());
            super.visit(pOpensDirective, pMetrics);
        }
    }
}
