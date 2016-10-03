/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@code AggregatedMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class AggregatedMetricsTest
{
    /**
     * A new {@code AggregatedMetrics} should have all-zero values.
     */
    @Test
    public void newInstanceIsEmpty()
    {
        // When
        AggregatedMetrics aMetrics = new AggregatedMetrics();

        // Then
        assertEquals(0, aMetrics.getNumPackages());
        assertEquals(0, aMetrics.getNumCompilationUnits());
        assertEquals(0, aMetrics.getNumTypes());
        assertEquals(0, aMetrics.getNumMethods());
        assertEquals(0, aMetrics.getNumFields());
        assertEquals(0, aMetrics.getNumStatements());
        assertEquals(0, aMetrics.getNumLineComments());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
    }


    /**
     * Calling {@code of(Iterable&lt;PackageMetrics&gt;)} should create an {@code AggregatedMetrics}
     * instance with the values from all of the package metrics.
     */
    @Test
    public void ofIterableCreatesInstanceWithPackageValues()
    {
        // Given
        PackageMetrics aPackage1 = new PackageMetrics("org.myire");
        aPackage1.add(new CompilationUnitMetrics("x.java"));
        aPackage1.add(new CompilationUnitMetrics("y.java"));
        aPackage1.add(new CompilationUnitMetrics("z.java"));
        PackageMetrics aPackage2 = new PackageMetrics("com.acme");
        aPackage2.add(new CompilationUnitMetrics("w.java"));
        aPackage2.add(new CompilationUnitMetrics("q.java"));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(Arrays.asList(aPackage1, aPackage2));

        // Then
        assertEquals(2, aAggregation.getNumPackages());
        assertEquals(5, aAggregation.getNumCompilationUnits());
    }


    /**
     * Calling {@code of(PackageMetrics)} should create an {@code AggregatedMetrics} instance with
     * the values from the package metrics.
     */
    @Test
    public void ofCreatesInstanceWithPackageValues()
    {
        // Given
        PackageMetrics aPackage = new PackageMetrics("org.myire");
        aPackage.add(new CompilationUnitMetrics("x.java"));
        aPackage.add(new CompilationUnitMetrics("y.java"));
        aPackage.add(new CompilationUnitMetrics("z.java"));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aPackage);

        // Then
        assertEquals(1, aAggregation.getNumPackages());
        assertEquals(3, aAggregation.getNumCompilationUnits());
    }


    /**
     * Calling {@code ofChildren(PackageMetrics)} should create an {@code AggregatedMetrics}
     * instance with the values from the package metrics but with the package count set to zero.
     */
    @Test
    public void ofChildrenCreatesInstanceWithPackageValues()
    {
        // Given
        PackageMetrics aPackage = new PackageMetrics("org.myire");
        aPackage.add(new CompilationUnitMetrics("x.java"));
        aPackage.add(new CompilationUnitMetrics("y.java"));
        aPackage.add(new CompilationUnitMetrics("z.java"));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.ofChildren(aPackage);

        // Then
        assertEquals(0, aAggregation.getNumPackages());
        assertEquals(3, aAggregation.getNumCompilationUnits());
    }


    /**
     * Adding a {@code PackageMetrics} instance to an aggregation should increment the package count
     * and the compilation unit related values.
     */
    @Test
    public void addPackageMetricsIncrementsPackageValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        PackageMetrics aPackage = new PackageMetrics("org.myire");
        aPackage.add(new CompilationUnitMetrics("x.java"));
        aPackage.add(new CompilationUnitMetrics("y.java"));
        aPackage.add(new CompilationUnitMetrics("z.java"));

        // When
        aAggregation.add(aPackage);

        // Then
        assertEquals(1, aAggregation.getNumPackages());
        assertEquals(3, aAggregation.getNumCompilationUnits());
    }


    /**
     * Calling {@code of(CompilationUnitMetrics)} should create an {@code AggregatedMetrics}
     * instance with the values from the compilation unit metrics
     */
    @Test
    public void ofCreatesInstanceWithCompilationUnitValues()
    {
        // Given
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withBlockComments(1, 6)
                .build();
        CompilationUnitMetrics aCompilationUnit = new CompilationUnitMetrics("x.java");
        aCompilationUnit.getComments().add(aComments);
        aCompilationUnit.add(new TypeMetrics("X", TypeMetrics.Kind.CLASS));
        aCompilationUnit.add(new TypeMetrics("Y", TypeMetrics.Kind.CLASS));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aCompilationUnit);

        // Then
        assertEquals(1, aAggregation.getNumCompilationUnits());
        assertEquals(2, aAggregation.getNumTypes());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code ofChildren(CompilationUnitMetrics)} should create an {@code AggregatedMetrics}
     * instance with the values from the compilation unit metrics but with the compilation unit
     * count set to zero.
     */
    @Test
    public void ofChildrenCreatesInstanceWithCompilationUnitValues()
    {
        // Given
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withBlockComments(1, 6)
                .build();
        CompilationUnitMetrics aCompilationUnit = new CompilationUnitMetrics("x.java");
        aCompilationUnit.getComments().add(aComments);
        aCompilationUnit.add(new TypeMetrics("X", TypeMetrics.Kind.CLASS));
        aCompilationUnit.add(new TypeMetrics("Y", TypeMetrics.Kind.CLASS));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.ofChildren(aCompilationUnit);

        // Then
        assertEquals(0, aAggregation.getNumCompilationUnits());
        assertEquals(2, aAggregation.getNumTypes());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Adding a {@code CompilationUnitMetrics} instance to an aggregation should increment the
     * compilation unit count and the type related values.
     */
    @Test
    public void addCompilationUnitMetricsIncrementsCompilationUnitValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withBlockComments(1, 6)
                .build();
        CompilationUnitMetrics aCompilationUnit = new CompilationUnitMetrics("x.java");
        aCompilationUnit.getComments().add(aComments);
        aCompilationUnit.add(new TypeMetrics("X", TypeMetrics.Kind.CLASS));
        aCompilationUnit.add(new TypeMetrics("Y", TypeMetrics.Kind.CLASS));

        // When
        aAggregation.add(aCompilationUnit);

        // Then
        assertEquals(1, aAggregation.getNumCompilationUnits());
        assertEquals(2, aAggregation.getNumTypes());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code of(TypeMetrics)} should create an {@code AggregatedMetrics} instance with the
     * values from the type metrics.
     */
    @Test
    public void ofCreatesInstanceWithTypeValues()
    {
        // Given
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(2)
                .withBlockComments(4, 10)
                .withJavaDocComments(1, 7)
                .build();
        TypeMetrics aType = new TypeMetrics("X", TypeMetrics.Kind.ANNOTATION);
        aType.add(new FieldMetrics("staticField", FieldMetrics.Kind.STATIC_FIELD));
        aType.add(new MethodMetrics("someMethod", MethodMetrics.Kind.INSTANCE_METHOD));
        aType.add(new TypeMetrics("Inner", TypeMetrics.Kind.CLASS));
        aType.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aType);

        // Then
        assertEquals(2, aAggregation.getNumTypes());
        assertEquals(1, aAggregation.getNumFields());
        assertEquals(1, aAggregation.getNumMethods());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code ofChildren(TypeMetrics)} should create an {@code AggregatedMetrics} instance
     * with the values from the type metrics but with the type count set to only the number of
     * inner types.
     */
    @Test
    public void ofChildrenCreatesInstanceWithTypeValues()
    {
        // Given
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(2)
                .withBlockComments(4, 10)
                .withJavaDocComments(1, 7)
                .build();
        TypeMetrics aType = new TypeMetrics("X", TypeMetrics.Kind.ANNOTATION);
        aType.add(new FieldMetrics("staticField", FieldMetrics.Kind.STATIC_FIELD));
        aType.add(new MethodMetrics("someMethod", MethodMetrics.Kind.INSTANCE_METHOD));
        aType.add(new TypeMetrics("Inner", TypeMetrics.Kind.CLASS));
        aType.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.ofChildren(aType);

        // Then
        assertEquals(1, aAggregation.getNumTypes());
        assertEquals(1, aAggregation.getNumFields());
        assertEquals(1, aAggregation.getNumMethods());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Adding a {@code TypeMetrics} instance to an aggregation should increment the type count
     * and the method, field, statement and comment related values.
     */
    @Test
    public void addTypeMetricsIncrementsTypeValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(2)
                .withBlockComments(4, 10)
                .withJavaDocComments(1, 7)
                .build();
        TypeMetrics aType = new TypeMetrics("X", TypeMetrics.Kind.ANNOTATION);
        aType.add(new FieldMetrics("staticField", FieldMetrics.Kind.STATIC_FIELD));
        aType.add(new FieldMetrics("instanceField", FieldMetrics.Kind.INSTANCE_FIELD));
        aType.add(new MethodMetrics("someMethod", MethodMetrics.Kind.INSTANCE_METHOD));
        aType.add(new TypeMetrics("Inner", TypeMetrics.Kind.CLASS));
        aType.getComments().add(aComments);

        // When
        aAggregation.add(aType);

        // Then
        assertEquals(2, aAggregation.getNumTypes());
        assertEquals(2, aAggregation.getNumFields());
        assertEquals(1, aAggregation.getNumMethods());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code of(MethodMetrics)} should create an {@code AggregatedMetrics} instance with
     * the values from the method metrics.
     */
    @Test
    public void ofCreatesInstanceWithMethodValues()
    {
        // Given
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(12)
                .withBlockComments(5, 28)
                .withJavaDocComments(1, 16)
                .build();
        MethodMetrics aMethod = new MethodMetrics("someMethod", MethodMetrics.Kind.STATIC_METHOD);
        aMethod.add(new TypeMetrics("Local", TypeMetrics.Kind.CLASS));
        aMethod.getStatements().add(aStatements);
        aMethod.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aMethod);

        // Then
        assertEquals(1, aAggregation.getNumMethods());
        assertEquals(1, aAggregation.getNumTypes());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code ofChildren(MethodMetrics)} should create an {@code AggregatedMetrics} instance
     * with the values from the method metrics but with the method count set to zero.
     */
    @Test
    public void ofChildrenCreatesInstanceWithMethodValues()
    {
        // Given
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(12)
                .withBlockComments(5, 28)
                .withJavaDocComments(1, 16)
                .build();
        MethodMetrics aMethod = new MethodMetrics("someMethod", MethodMetrics.Kind.STATIC_METHOD);
        aMethod.add(new TypeMetrics("Local", TypeMetrics.Kind.CLASS));
        aMethod.getStatements().add(aStatements);
        aMethod.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.ofChildren(aMethod);

        // Then
        assertEquals(0, aAggregation.getNumMethods());
        assertEquals(1, aAggregation.getNumTypes());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Adding a {@code MethodMetrics} instance to an aggregation should increment the method count
     * and the statement and comment related values.
     */
    @Test
    public void addMethodMetricsIncrementsMethodValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(12)
                .withBlockComments(5, 28)
                .withJavaDocComments(1, 16)
                .build();
        MethodMetrics aMethod = new MethodMetrics("someMethod", MethodMetrics.Kind.STATIC_METHOD);
        aMethod.add(new TypeMetrics("Local", TypeMetrics.Kind.CLASS));
        aMethod.getStatements().add(aStatements);
        aMethod.getComments().add(aComments);

        // When
        aAggregation.add(aMethod);

        // Then
        assertEquals(1, aAggregation.getNumMethods());
        assertEquals(1, aAggregation.getNumTypes());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code of(FieldMetrics)} should create an {@code AggregatedMetrics} instance with the
     * values from the field metrics.
     */
    @Test
    public void ofCreatesInstanceWithFieldValues()
    {
        // Given
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withJavaDocComments(1, 16)
                .build();
        FieldMetrics aField = new FieldMetrics("X", FieldMetrics.Kind.ENUM_CONSTANT);
        aField.getStatements().add(aStatements);
        aField.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aField);

        // Then
        assertEquals(1, aAggregation.getNumFields());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Calling {@code ofChildren(FieldMetrics)} should create an {@code AggregatedMetrics} instance
     * with the values from the field metrics but with the field count set to zero.
     */
    @Test
    public void ofChildrenCreatesInstanceWithFieldValues()
    {
        // Given
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withJavaDocComments(1, 16)
                .build();
        FieldMetrics aField = new FieldMetrics("X", FieldMetrics.Kind.ENUM_CONSTANT);
        aField.getStatements().add(aStatements);
        aField.getComments().add(aComments);

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.ofChildren(aField);

        // Then
        assertEquals(0, aAggregation.getNumFields());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Adding a {@code FieldMetrics} instance to an aggregation should increment the field count and
     * the statement and comment related values.
     */
    @Test
    public void addFieldMetricsIncrementsFieldValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(1);
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withJavaDocComments(1, 16)
                .build();
        FieldMetrics aField = new FieldMetrics("X", FieldMetrics.Kind.ENUM_CONSTANT);
        aField.getStatements().add(aStatements);
        aField.getComments().add(aComments);

        // When
        aAggregation.add(aField);

        // Then
        assertEquals(1, aAggregation.getNumFields());
        assertEquals(aStatements.getNumStatements(), aAggregation.getNumStatements());
        assertEquals(aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }


    /**
     * Adding a {@code StatementMetrics} instance to an aggregation should increment the statement
     * related values.
     */
    @Test
    public void addStatementMetricsIncrementsStatementValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        StatementMetrics aStatements = StatementMetricsTest.createStatementMetrics(17);

        // When
        aAggregation.add(aStatements);
        aAggregation.add(aStatements);

        // Then
        assertEquals(2 * aStatements.getNumStatements(), aAggregation.getNumStatements());
    }


    /**
     * Adding a {@code CommentMetrics} instance to an aggregation should increment the comment
     * related values.
     */
    @Test
    public void addCommentMetricsIncrementsCommentValues()
    {
        // Given
        AggregatedMetrics aAggregation = new AggregatedMetrics();
        CommentMetrics aComments =
                new CommentMetricsTest.Builder()
                .withNumLineComments(14)
                .withBlockComments(22, 75)
                .withJavaDocComments(12, 900)
                .build();

        // When
        aAggregation.add(aComments);
        aAggregation.add(aComments);

        // Then
        assertEquals(2 * aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(2 * aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(2 * aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(2 * aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(2 * aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
    }
}
