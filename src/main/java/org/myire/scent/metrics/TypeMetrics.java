/*
 * Copyright 2016, 2018, 2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Source code metrics for a class, an interface, an enum, an annotation, or a record. These metrics
 * are a container for the metrics of the type's fields, methods and inner types.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class TypeMetrics extends CodeElementMetrics
{
    private final Kind fKind;
    private final Collection<FieldMetrics> fFields = new ArrayList<>();
    private final Collection<MethodMetrics> fMethods = new ArrayList<>();
    private final Collection<TypeMetrics> fInnerTypes = new ArrayList<>();


    /**
     * Create a new {@code TypeMetrics}.
     *
     * @param pName The name of the type.
     * @param pKind The type's kind.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public TypeMetrics(@Nonnull String pName, @Nonnull Kind pKind)
    {
        super(pName);
        fKind = requireNonNull(pKind);
    }


    /**
     * Get the type's kind.
     *
     * @return  The type's kind, never null.
     */
    @Nonnull
    public Kind getKind()
    {
        return fKind;
    }


    /**
     * Get the number of fields defined within the type.
     *
     * @return  The number of fields.
     */
    public int getNumFields()
    {
        return fFields.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each field defined within the
     * type.
     *
     * @return  An {@code Iterable} for the fields' metrics, never null.
     */
    @Nonnull
    public Iterable<FieldMetrics> getFields()
    {
        return fFields;
    }


    /**
     * Get the number of methods defined within the type.
     *
     * @return  The number of methods.
     */
    public int getNumMethods()
    {
        return fMethods.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each method defined within the
     * type.
     *
     * @return  An {@code Iterable} for the methods' metrics, never null.
     */
    @Nonnull
    public Iterable<MethodMetrics> getMethods()
    {
        return fMethods;
    }


    /**
     * Get the number of inner types defined within this type.
     *
     * @return  The number of inner types.
     */
    public int getNumInnerTypes()
    {
        return fInnerTypes.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each inner type defined within
     * this type.
     *
     * @return  An {@code Iterable} for the inner types' metrics, never null.
     */
    @Nonnull
    public Iterable<TypeMetrics> getInnerTypes()
    {
        return fInnerTypes;
    }


    /**
     * Add metrics for a field to this instance.
     *
     * @param pField    The field metrics.
     *
     * @throws NullPointerException if {@code pField} is null.
     */
    public void add(@Nonnull FieldMetrics pField)
    {
        fFields.add(requireNonNull(pField));
    }


    /**
     * Add metrics for a method to this instance.
     *
     * @param pMethod   The method metrics.
     *
     * @throws NullPointerException if {@code pMethod} is null.
     */
    public void add(@Nonnull MethodMetrics pMethod)
    {
        fMethods.add(requireNonNull(pMethod));
    }


    /**
     * Add metrics for an inner type to this instance.
     *
     * @param pInnerType    The inner type metrics.
     *
     * @throws NullPointerException if {@code pInnerType} is null.
     */
    public void add(@Nonnull TypeMetrics pInnerType)
    {
        fInnerTypes.add(requireNonNull(pInnerType));
    }


    /**
     * The kinds of type for which metrics are collected.
     */
    public enum Kind
    {
        /** A normal class. */
        CLASS,

        /** A normal interface. */
        INTERFACE,

        /** An enum class. */
        ENUM,

        /**
         * An enum constant with a class body, which effectively is a subclass of the enclosing
         * enum.
         */
        ENUM_CONSTANT,

        /** An annotation interface. */
        ANNOTATION,

        /** An anonymous class. */
        ANONYMOUS_CLASS,

        /** A record class. */
        RECORD
    }
}
