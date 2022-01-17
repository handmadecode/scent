/* File header block comment 1 */
/* File header block comment 2 */
package org.myire.scent;

// Single line comment for the enum
enum FullEnum
{
    /** JavaDoc for constant 1. */
    ENUM_CONSTANT_1,

    /**
     * JavaDoc for constant 2, which effectively is a subclass.
     */
    ENUM_CONSTANT_2
    {
        @Override
        int instanceMethod()
        {
            return 2;
        }
    }
    ;

    // Single line comment for a static field
    static int staticField;

    /* Single block comment for an instance field */
    int instanceField;


    static
    {
        staticField = 17;
    }


    {
        instanceField = -99;
    }


    // Single line comment for the constructor
    private FullEnum()
    {
        instanceField--;
    }


    /**
     * A method JavaDoc with
     * eight lines.
     *<p>
     * A stray line.
     *
     * @return An int.
     */
    int instanceMethod()
    {
        return 1;
    }


    /** A method JavaDoc with two lines.
     * @return An int. */
    static int classMethod()
    {
        return 1;
    }


    /*
     * Three-line block comment for the inner interface.
     */
    interface InnerInterface
    {
        int get();
    }


    // Two single line comments
    // for the inner class
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
     * JavaDoc for inner enum.
     *<code>
     * x=1;
     *</code>
     * Seven lines.
     */
    static enum InnerEnum {
        // Single line comment for enum constant
        INNER_CONSTANT_1,
        /* Block comment for enum constant. */
        INNER_CONSTANT_2
    }


    @interface InnerAnnotation
    {
        double factor();
    }

    record InnerRecord(int recordField) {}
}
