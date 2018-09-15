/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.util.CommentTestUtil;


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
        assertEquals(0, aMetrics.getLineCommentsLength());
        assertEquals(0, aMetrics.getNumBlockComments());
        assertEquals(0, aMetrics.getNumBlockCommentLines());
        assertEquals(0, aMetrics.getBlockCommentsLength());
        assertEquals(0, aMetrics.getNumJavaDocComments());
        assertEquals(0, aMetrics.getNumJavaDocLines());
        assertEquals(0, aMetrics.getJavaDocCommentsLength());
    }


    /**
     * Calling {@code of(JavaMetrics)} should create an {@code AggregatedMetrics} instance with the
     * values from the Java metrics.
     */
    @Test
    public void ofCreatesInstanceWithJavaMetricsValues()
    {
        // Given
        JavaMetrics aJavaMetrics = new JavaMetrics();
        PackageMetrics aPackage1 = aJavaMetrics.maybeCreate("org.myire");
        aPackage1.add(new CompilationUnitMetrics("x.java"));
        aPackage1.add(new CompilationUnitMetrics("y.java"));
        aPackage1.add(new CompilationUnitMetrics("z.java"));
        PackageMetrics aPackage2 = aJavaMetrics.maybeCreate("com.acme");
        aPackage2.add(new CompilationUnitMetrics("w.java"));
        aPackage2.add(new CompilationUnitMetrics("q.java"));

        // When
        AggregatedMetrics aAggregation = AggregatedMetrics.of(aJavaMetrics);

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
                new CommentTestUtil.Builder()
                .withBlockComments(1, 6, 24)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withBlockComments(1, 6, 13)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withBlockComments(2, 15, 93)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(2, 21)
                .withBlockComments(4, 10, 99)
                .withJavaDocComments(1, 7, 30)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(2, 19)
                .withBlockComments(4, 10, 28)
                .withJavaDocComments(3, 21, 50)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(5, 70)
                .withBlockComments(1, 2, 14)
                .withJavaDocComments(3, 48, 109)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(12, 81)
                .withBlockComments(5, 28, 66)
                .withJavaDocComments(1, 16, 54)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(4, 61)
                .withBlockComments(1, 12, 86)
                .withJavaDocComments(2, 16, 31)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(2, 27)
                .withBlockComments(2, 2, 23)
                .withJavaDocComments(4, 52, 200)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withJavaDocComments(2, 19, 98)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withJavaDocComments(2, 45, 512)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withJavaDocComments(4, 41, 107)
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
        assertEquals(aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
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
                new CommentTestUtil.Builder()
                .withLineComments(14, 89)
                .withBlockComments(22, 75, 2143)
                .withJavaDocComments(12, 900, 16430)
                .build();

        // When
        aAggregation.add(aComments);
        aAggregation.add(aComments);

        // Then
        assertEquals(2 * aComments.getNumLineComments(), aAggregation.getNumLineComments());
        assertEquals(2 * aComments.getLineCommentsLength(), aAggregation.getLineCommentsLength());
        assertEquals(2 * aComments.getNumBlockComments(), aAggregation.getNumBlockComments());
        assertEquals(2 * aComments.getNumBlockCommentLines(), aAggregation.getNumBlockCommentLines());
        assertEquals(2 * aComments.getBlockCommentsLength(), aAggregation.getBlockCommentsLength());
        assertEquals(2 * aComments.getNumJavaDocComments(), aAggregation.getNumJavaDocComments());
        assertEquals(2 * aComments.getNumJavaDocLines(), aAggregation.getNumJavaDocLines());
        assertEquals(2 * aComments.getJavaDocCommentsLength(), aAggregation.getJavaDocCommentsLength());
    }
}
