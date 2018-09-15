/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * An aggregation of several source code metrics instances.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class AggregatedMetrics
{
    private int fNumPackages;
    private int fNumCompilationUnits;
    private int fNumTypes;
    private int fNumMethods;
    private int fNumFields;
    private int fNumStatements;
    private int fNumLineComments;
    private int fLineCommentsLength;
    private int fNumBlockComments;
    private int fNumBlockCommentLines;
    private int fBlockCommentsLength;
    private int fNumJavaDocComments;
    private int fNumJavaDocLines;
    private int fJavaDocCommentsLength;


    /**
     * Create a new {@code AggregatedMetrics} and add the values from a {@code JavaMetrics} to the
     * aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull JavaMetrics pValues)
    {
        AggregatedMetrics aMetrics = new AggregatedMetrics();
        for (PackageMetrics aPackage : pValues.getPackages())
            aMetrics.add(aPackage);

        return aMetrics;
    }


    /**
     * Create a new {@code AggregatedMetrics} and add the values from a {@code PackageMetrics} to
     * the aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull PackageMetrics pValues)
    {
        return new AggregatedMetrics().add(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add values from the compilation units of a
     * {@code PackageMetrics} to the aggregation. This differs from calling
     * {@link #of(PackageMetrics)} in that the aggregated number of packages isn't incremented by
     * this method.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics ofChildren(@Nonnull PackageMetrics pValues)
    {
        return new AggregatedMetrics().addChildren(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add the values from a
     * {@code CompilationUnitMetrics} to the aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull CompilationUnitMetrics pValues)
    {
        return new AggregatedMetrics().add(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add values from the comments and types of a
     * {@code CompilationUnitMetrics} to the aggregation. This differs from calling
     * {@link #of(CompilationUnitMetrics)} in that the aggregated number of compilation units isn't
     * incremented by this method.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics ofChildren(@Nonnull CompilationUnitMetrics pValues)
    {
        return new AggregatedMetrics().addChildren(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add the values from a {@code TypeMetrics} to the
     * aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull TypeMetrics pValues)
    {
        return new AggregatedMetrics().add(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add values from the comments and members of a
     * {@code TypeMetrics} to the aggregation. This differs from calling {@link #of(TypeMetrics)} in
     * that the aggregated number of types is only incremented for the type's inner types, not for
     * the type itself.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics ofChildren(@Nonnull TypeMetrics pValues)
    {
        return new AggregatedMetrics().addChildren(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add the values from a {@code MethodMetrics} to the
     * aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull MethodMetrics pValues)
    {
        return new AggregatedMetrics().add(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add values from the comments and statements of a
     * {@code MethodMetrics} to the aggregation. This differs from calling
     * {@link #of(MethodMetrics)} in that the aggregated number of methods isn't incremented by this
     * method.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics ofChildren(@Nonnull MethodMetrics pValues)
    {
        return new AggregatedMetrics().addChildren(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add the values of a {@code FieldMetrics} to the
     * aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return    A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics of(@Nonnull FieldMetrics pValues)
    {
        return new AggregatedMetrics().add(pValues);
    }


    /**
     * Create a new {@code AggregatedMetrics} and add values from the comments and statements of a
     * {@code FieldMetrics} to the aggregation. This differs from calling {@link #of(FieldMetrics)}
     * in that the aggregated number of fields isn't incremented by this method.
     *
     * @param pValues   The values to add.
     *
     * @return  A new {@code AggregatedMetrics}, never null.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    static public AggregatedMetrics ofChildren(@Nonnull FieldMetrics pValues)
    {
        return new AggregatedMetrics().addChildren(pValues);
    }


    /**
     * Get the number of packages in this aggregation.
     *
     * @return  The number of packages.
     */
    public int getNumPackages()
    {
        return fNumPackages;
    }


    /**
     * Get the number of compilation units in this aggregation.
     *
     * @return  The number of compilation units.
     */
    public int getNumCompilationUnits()
    {
        return fNumCompilationUnits;
    }


    /**
     * Get the number of types in this aggregation.
     *
     * @return  The number of types.
     */
    public int getNumTypes()
    {
        return fNumTypes;
    }


    /**
     * Get the number of methods in this aggregation.
     *
     * @return  The number of methods.
     */
    public int getNumMethods()
    {
        return fNumMethods;
    }


    /**
     * Get the number of fields in this aggregation.
     *
     * @return  The number of fields.
     */
    public int getNumFields()
    {
        return fNumFields;
    }


    /**
     * Get the number of statements in this aggregation.
     *
     * @return  The number of statements.
     */
    public int getNumStatements()
    {
        return fNumStatements;
    }


    /**
     * Get the number of line comments in this aggregation.
     *
     * @return  The number of line comments.
     */
    public int getNumLineComments()
    {
        return fNumLineComments;
    }


    /**
     * Get the length of the line comments' content in this aggregation. The length is the number
     * of characters remaining when leading and trailing whitespace has been trimmed away from the
     * comments.
     *
     * @return  The content length of the line comments.
     */
    public int getLineCommentsLength()
    {
        return fLineCommentsLength;
    }


    /**
     * Get the number of block comments in this aggregation.
     *
     * @return  The number of block comments.
     */
    public int getNumBlockComments()
    {
        return fNumBlockComments;
    }


    /**
     * Get the number of block comment lines in this aggregation.
     *
     * @return  The number of block comment lines.
     */
    public int getNumBlockCommentLines()
    {
        return fNumBlockCommentLines;
    }


    /**
     * Get the length of the block comments' content in this aggregation. The length is the number
     * of characters remaining when leading and trailing whitespace and asterisks have been trimmed
     * away from each line in the comments.
     *
     * @return  The content length of the block comments.
     */
    public int getBlockCommentsLength()
    {
        return fBlockCommentsLength;
    }


    /**
     * Get the number of JavaDoc comments in this aggregation.
     *
     * @return  The number of JavaDoc comments.
     */
    public int getNumJavaDocComments()
    {
        return fNumJavaDocComments;
    }


    /**
     * Get the number of JavaDoc comment lines in this aggregation.
     *
     * @return  The number of JavaDoc comment lines.
     */
    public int getNumJavaDocLines()
    {
        return fNumJavaDocLines;
    }


    /**
     * Get the length of the JavaDoc comments' content in this aggregation. The length is the number
     * of characters remaining when leading and trailing whitespace and asterisks have been trimmed
     * away from each line in the comments.
     *
     * @return  The content length of the JavaDoc comments.
     */
    public int getJavaDocCommentsLength()
    {
        return fJavaDocCommentsLength;
    }


    /**
     * Add a {@code PackageMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull PackageMetrics pValues)
    {
        fNumPackages++;
        return addChildren(pValues);
    }


    /**
     * Add the compilation units of a {@code PackageMetrics} to this aggregation.
     *
     * @param pValues   The instance to add the children from.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics addChildren(@Nonnull PackageMetrics pValues)
    {
        for (CompilationUnitMetrics aCompilationUnit : pValues.getCompilationUnits())
            add(aCompilationUnit);

        return this;
    }


    /**
     * Add a {@code CompilationUnitMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull CompilationUnitMetrics pValues)
    {
        fNumCompilationUnits++;
        return addChildren(pValues);
    }


    /**
     * Add the comments and types of a {@code CompilationUnitMetrics} to this aggregation.
     *
     * @param pValues   The instance to add the children from.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics addChildren(@Nonnull CompilationUnitMetrics pValues)
    {
        add(pValues.getComments());

        for (TypeMetrics aType : pValues.getTypes())
            add(aType);

        return this;
    }


    /**
     * Add a {@code TypeMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull TypeMetrics pValues)
    {
        fNumTypes++;
        return addChildren(pValues);
    }


    /**
     * Add the comments and members of a {@code TypeMetrics} to this aggregation.
     *
     * @param pValues   The instance to add the children from.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics addChildren(@Nonnull TypeMetrics pValues)
    {
        add(pValues.getComments());

        for (MethodMetrics aMethod : pValues.getMethods())
            add(aMethod);

        for (FieldMetrics aField : pValues.getFields())
            add(aField);

        for (TypeMetrics aInnerType : pValues.getInnerTypes())
            add(aInnerType);

        return this;
    }


    /**
     * Add a {@code MethodMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull MethodMetrics pValues)
    {
        fNumMethods++;
        return addChildren(pValues);
    }


    /**
     * Add the statements and comments of a {@code MethodMetrics} to this aggregation.
     *
     * @param pValues   The instance to add the children from.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics addChildren(@Nonnull MethodMetrics pValues)
    {
        for (TypeMetrics aLocalType : pValues.getLocalTypes())
            add(aLocalType);

        add(pValues.getStatements());
        add(pValues.getComments());
        return this;
    }


    /**
     * Add a {@code FieldMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull FieldMetrics pValues)
    {
        fNumFields++;
        return addChildren(pValues);
    }


    /**
     * Add the statements and comments of a {@code FieldMetrics} to this aggregation.
     *
     * @param pValues   The instance to add the children from.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics addChildren(@Nonnull FieldMetrics pValues)
    {
        add(pValues.getStatements());
        add(pValues.getComments());
        return this;
    }


    /**
     * Add the values of a {@code StatementMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull StatementMetrics pValues)
    {
        fNumStatements += pValues.getNumStatements();
        return this;
    }


    /**
     * Add the values of a {@code CommentMetrics} to this aggregation.
     *
     * @param pValues   The values to add.
     *
     * @return  This instance.
     *
     * @throws NullPointerException if {@code pValues} is null.
     */
    @Nonnull
    public AggregatedMetrics add(@Nonnull CommentMetrics pValues)
    {
        fNumLineComments += pValues.getNumLineComments();
        fLineCommentsLength += pValues.getLineCommentsLength();
        fNumBlockComments += pValues.getNumBlockComments();
        fNumBlockCommentLines += pValues.getNumBlockCommentLines();
        fBlockCommentsLength += pValues.getBlockCommentsLength();
        fNumJavaDocComments += pValues.getNumJavaDocComments();
        fNumJavaDocLines += pValues.getNumJavaDocLines();
        fJavaDocCommentsLength += pValues.getJavaDocCommentsLength();
        return this;
    }
}
