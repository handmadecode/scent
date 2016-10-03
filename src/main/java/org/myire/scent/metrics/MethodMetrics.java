/*
 * Copyright 2016 Peter Franzen. All rights reserved.
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
 * Source code metrics for a constructor, initializer, or method.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class MethodMetrics extends StatementElementMetrics
{
    private final Kind fKind;
    private final Collection<TypeMetrics> fLocalTypes = new ArrayList<>();


    /**
     * Create a new {@code MethodMetrics}.
     *
     * @param pName The name of the method.
     * @param pKind The method's kind.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public MethodMetrics(@Nonnull String pName, @Nonnull Kind pKind)
    {
        super(pName);
        fKind = requireNonNull(pKind);
    }


    /**
     * Get the method's kind.
     *
     * @return  The method's kind, never null.
     */
    @Nonnull
    public Kind getKind()
    {
        return fKind;
    }


    /**
     * Get the number of local types defined in the method.
     *
     * @return  The number of local types.
     */
    public int getNumLocalTypes()
    {
        return fLocalTypes.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each local type defined in the
     * method.
     *
     * @return  An {@code Iterable} for the local types' metrics, never null.
     */
    @Nonnull
    public Iterable<TypeMetrics> getLocalTypes()
    {
        return fLocalTypes;
    }


    /**
     * Add metrics for a local type to this instance.
     *
     * @param pLocalType    The local type metrics.
     *
     * @throws NullPointerException if {@code pLocalType} is null.
     */
    public void add(@Nonnull TypeMetrics pLocalType)
    {
        fLocalTypes.add(requireNonNull(pLocalType));
    }


    /**
     * The kinds of method for which metrics are collected.
     */
    public enum Kind
    {
        /** A constructor in a class. */
        CONSTRUCTOR,

        /** A non-static initializer in a class or an enum. */
        INSTANCE_INITIALIZER,

        /** A static initializer in a class or an enum. */
        STATIC_INITIALIZER,

        /** A non-static method in a class or an enum. */
        INSTANCE_METHOD,

        /** A static method in a class, interface or an enum. */
        STATIC_METHOD,

        /** An abstract method in a class, interface, or enum. */
        ABSTRACT_METHOD,

        /** A default method in an interface. */
        DEFAULT_METHOD,
    }
}
