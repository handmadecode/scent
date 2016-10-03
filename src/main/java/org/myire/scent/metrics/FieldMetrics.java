/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Source code metrics for a field, an enum constant or an annotation type element.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class FieldMetrics extends StatementElementMetrics
{
    private final Kind fKind;


    /**
     * Create a new {@code FieldMetrics}.
     *
     * @param pName The name of the field.
     * @param pKind The field's kind.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public FieldMetrics(@Nonnull String pName, @Nonnull Kind pKind)
    {
        super(pName);
        fKind = requireNonNull(pKind);
    }


    /**
     * Get the field's kind.
     *
     * @return  The field's kind, never null.
     */
    @Nonnull
    public Kind getKind()
    {
        return fKind;
    }


    /**
     * The kinds of field for which metrics are collected.
     */
    public enum Kind
    {
        /** A static field in a class, interface, enum or annotation. */
        STATIC_FIELD,

        /** A non-static field in a class or an enum. */
        INSTANCE_FIELD,

        /** An enum constant without a class body, which effectively is a static field. */
        ENUM_CONSTANT,

        /** An annotation type element. */
        ANNOTATION_TYPE_ELEMENT
    }
}
