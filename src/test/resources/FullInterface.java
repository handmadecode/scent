// Single line file header
package org.myire.scent;

/* Block comment 1 for interface */
/* Block comment 2 for interface */
interface FullInterface
{
    // Single line comment for a static field
    static int staticField1 = 4711;

    // Three single line comments
    // for an implicitly
    // static field
    int staticField2 = 17;


    /**
     * Five-line JavaDoc for interface method.
     *
     * @return  An int.
     */
    int interfaceMethod();


    // Single line comments that
    // span two lines.
    default int defaultMethod()
    {
        return 19;
    }


    /* Block comment 1 for method */
    /* Block comment 2 for method */
    /* Block comment 3 for method */
    /* Block comment 4 for method */
    static int classMethod()
    {
        return 1;
    }


    // Private methods in interfaces are allowed starting with Java 9
    private long privateMethod()
    {
        return 4711L;
    }


    /*
     * Three-line block comment for the inner interface.
     */
    interface InnerInterface
    {
        int get();
    }


    /**
     * JavaDoc for inner class.
     *<p>
     * Five lines.
     */
    static class InnerClass implements InnerInterface
    {
        int innerField = 17;

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


    static enum InnerEnum {
        // Single line comment for enum constant
        ENUM_CONSTANT_1,
        /** JavaDoc for enum constant. */
        ENUM_CONSTANT_2
    }


    @interface InnerAnnotation
    {
        String name() default "X";
    }
}
