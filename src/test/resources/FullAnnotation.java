/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;


@interface FullAnnotation
{
    static int staticField1 = 9;

    // Fields are implicitly static in an annotation
    int staticField2 = 9;


    /**
     * This annotation element does
     * not have a default value.
     *
     * @return An int.
     */
    int annotationElement();


    /**
     * This annotation element has a default value.
     *
     * @return 9.
     */
    int annotationElementWithDefault() default 9;


    interface InnerInterface
    {
        int get();
    }


    static class InnerClass implements InnerInterface
    {
        int innerField;

        InnerClass(int pParam)
        {
            innerField = pParam;
        }

        @Override
        public int get()
        {
            return innerField;
        }
    }


    /**
     * JavaDoc for inner enum
     * has four lines.
     */
    static enum InnerEnum {
        // Single line comment for enum constant
        ENUM_CONSTANT_1,
        /** JavaDoc for enum constant. */
        ENUM_CONSTANT_2
    }


    /*
     * Block comment for the inner annotation.
     */
    @interface InnerAnnotation
    {
        boolean flag();
    }

//    Pending https://github.com/javaparser/javaparser/issues/3260
//    record InnerRecord(int recordField) {}
}
