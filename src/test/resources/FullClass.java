/*
 * Three lines of file header
 */
package org.myire.scent;

/**
 * Four lines of JavaDoc for
 * the class
 */
class FullClass
{
    // Single line comment for a static field
    static int staticField;

    /* Two-line block comment
       for an instance field */
    int instanceField1;

    int instanceField2 = 77;


    static
    {
        staticField = 17;
    }


    /* Three block comments */
    /* for the */
    /* instance initialier */
    {
        instanceField1 = -99;
    }


    // Single line comment for the constructor
    public FullClass()
    {
        instanceField2 += staticField;
    }


    /**
     * A method JavaDoc with
     * five lines.
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


    void methodWithLocalClass()
    {
        /* Block comment for local class */
        class LocalClass
        {
            int fLocalClassField = 17;
        }

        LocalClass aLocalClass = new LocalClass();
    }


    static Object methodWithAnonymousClass()
    {
        return new AutoCloseable()
        {
            @Override
            public void close() throws Exception
            {
                // This method prints
                System.out.println();
            }
        };
    }


    /*
     * Three-line block comment for the inner interface.
     */
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


    static enum InnerEnum {
        // Single line comment for enum constant
        ENUM_CONSTANT_1,
        /** JavaDoc for enum constant. */
        ENUM_CONSTANT_2
    }


    /**
     * JavaDoc for inner annotation.
     */
    @interface InnerAnnotation
    {
        int value();
    }
}
