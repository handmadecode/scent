// Full record example
package org.myire.scent;

/**
 * Record class JavaDoc.
 */
public record FullRecord(int instanceField1, String instanceField2)
{
    // Single line comment for a static field
    static int staticField;


    static
    {
        staticField = 17;
    }

    // Compact canonical constructor
    public FullRecord
    {
        staticField += instanceField1 + instanceField2.length();
    }

    public FullRecord()
    {
        this(1, "2");
    }

    /**
     * Overridden getter.
     * @return The f2 field.
     */
    @Override
    public String f2()
    {
        staticField++;
        return instanceField2;
    }


    /**
     * A method JavaDoc with
     * five lines.
     * @return An int.
     */
    int instanceMethod()
    {
        return instanceField1 + 1;
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


    static class InnerClass implements FullRecord.InnerInterface
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

    enum InnerEnum {
        // Single line comment for enum constant
        ENUM_CONSTANT_1,
        /** JavaDoc for enum constant. */
        ENUM_CONSTANT_2
    }


    @interface InnerAnnotation
    {
        int name();
    }

    // Pending https://github.com/javaparser/javaparser/issues/3260
    /*
    public record InnerRecord(int recordField)
    {

    }
    */
}
