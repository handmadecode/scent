# Scent

A Java library for collecting metrics from Java source code.

Scent parses Java source code and collects various metrics from it. The collected metrics contain the
number of packages, compilation units, types, methods, and fields, as well as the number of comments
and statements.

Scent was created as a replacement for [JavaNCSS](https://github.com/codehaus/javancss), a tool that
the author had used happily for over ten years. After March 2014 this happiness receded due to the
lack of support for Java 1.8 code, and eventually the need for a new tool became strong enough to
warrant yet another yak shaving project. 

Note that Scent is not a drop-in replacement for JavaNCSS, nor does it produce exactly the same
metrics. See the [Differences with JavaNCSS](#differences-with-javancss) section for more details on
how Scent and JavaNCSS differ.

Scent must compiled with at least Java 9, but can still be run with Java 8. It parses and collects
metrics for source code up to and including language level 11.


## Contents
1. [Release Notes](#release-notes)
1. [General Usage](#general-usage)
1. [Collected Metrics](#collected-metrics)
1. [Dependencies](#dependencies)
1. [Differences with JavaNCSS](#differences-with-javancss)


## Release Notes

### version 2.0

* Support for language levels 9, 10, and 11.
* Requires Java 8 or higher to run.
* Packaged as a modular jar file. The classes in the jar file are still targeted for Java 8.
* Private interface methods are categorized as instance methods, not as abstract methods.
* Native methods are categorized separately from instance methods.
* The length, i.e. the number of characters, are collected for each comment.
* Comments in `package-info.java` files are collected and associated with the package.
* Report format and output file can be specified in the arguments to `org.myire.scent.Main::main`.
* API breaking change: `JavaMetricsCollector::getCollectedMetrics()` returns a `JavaMetrics`
instance, not an `Iterable<PackageMetrics>`.
* API breaking change: `org.myire.scent.MetricsPrinter` has been removed and is replaced by
`org.myire.scent.report.MetricsReportWriter` implementations, which can create text, xml, html and
xsl reports.

### version 1.0

* Comments ending on the same line as a method are assigned to the method, not to the enclosing
type.

### version 0.9

* Initial release.


## General Usage

### From code

The central class in Scent is `org.myire.scent.collect.JavaMetricsCollector`. To collect metrics for
a compilation unit (e.g. a Java source file), call the method
`collect(String, InputStream, Charset)`. This method will parse the source code from the specified
`InputStream` and collect its metrics. The metrics are not returned immediately, instead they are
stored in the `JavaMetricsCollector` instance. This allows collecting metrics for multiple
compilation units before getting the metrics from the `JavaMetricsCollector`.

Once all compilation units have been passed to `collect`, the metrics can be retrieved with a call
to `getCollectedMetrics`. This method returns a `JavaMetrics` instance, which contains metrics for
each unique Java package declared by the ordinary compilation units passed to `collect`. These
package metrics contain all other collected metrics, see the section
[Collected Metrics](#collected-metrics) for details on the various types of metrics that are
collected.

The `JavaMetrics` instance also contains metrics for the module declaration in any modular
compilation unit passed to `JavaMetricsCollector::collect`.

The pattern for collecting metrics is something like

    Collection<InputStream> inputStreams = ...
    JavaMetricsCollector c = new JavaMetricsCollector();
    for (InputStream is : inputStreams)
    {
        String compilationUnitName = ...
        Charset cs = ...
        c.collect(compilationUnitName, is, cs);
    }

    JavaMetrics m = c.getCollectedMetrics();
    for (PackageMetrics p : m.getPackages())
        ...
    for (ModularCompilationUnitMetrics mcu : m.getModularCompilationUnits())
        ...

The class `org.myire.scent.Main` is an example of how to collect metrics for Java source code files.
The method `main` will collect metrics from all files ending in ".java" found in the path(s)
specified as argument(s) to the method, recursively descending into subdirectories.

### Running the jar file

The scent jar file can be used as a tool for collecting and printing metrics for Java source code.
Note that the `javaparser-core` jar file must be on the class path, see the
[Dependencies](#dependencies) section.

For example,

    java -cp ... org.myire.scent.Main SomeClass.java

would collect and print metrics for the file `SomeClass.java` in the current directory, whereas

    java -cp ... org.myire.scent.Main src/main/java src/test/java

would collect and print metrics for the main and test Java code when run from the root directory of
a project with a standard Maven layout.

The `org.myire.scent.Main` class is specified as the main class of the Scent jar file, and the
`javaparser-core` jar file is listed in the manifest's `Class-Path` attribute. This means that if
the `javaparser-core` jar file is located next to the scent jar file, it is sufficient to run the
command

    java -jar scent-x.y.jar <paths>

where `x.y` is the version number part of the scent jar file.

The scent jar file is a modular jar file. If both that file and the `javaparser-core` jar file are
on the module path, for instance in a directory called `modules`, the `Main` class can also be run
with the command

    java -p modules -m org.myire.scent <paths>

#### Main options

By default, the `Main` class writes a report of the collected metrics on plain text format to the
console. The format and destination of this report can be specified through options passed in the
arguments to the `Main` class.

The synopsis for the arguments to `Main` are:

    [-text | -xml | -html | -xsl xsl-file] [-o output-file] <paths>

where the options are

* `-text`: report the collected metrics on plain text format
* `-xml`: report the collected metrics on xml format
* `-html`: report the collected metrics on html format
* `-xsl xsl-file`: report the collected metrics by applying the specified xsl file to an
intermediate xml report
* `-o output-file`: write the report to the specified file

If no format is specified, the plain text format will be used. If multiple formats are specified,
the last will take precedence. If no output file is specified, the report will be written to the
console.

## Collected Metrics

Scent collects source code metrics on different levels. Each level contains metrics for the level
itself as well as metrics for its sub-levels.

There are two top-level metrics, package metrics and modular compilation unit metrics.

### Modular compilation unit

Scent creates an instance of `org.myire.scent.metrics.ModularCompilationUnitMetrics` for each
modular compilation unit passed to `JavaMetricsCollector::collect`. These
`ModularCompilationUnitMetrics` instances are returned by `JavaMetrics::getModularCompilationUnits`
and each of them contains metrics for the module declaration and for comments associated with the
compilation unit itself, such as file headers.

### Module declaration

The metrics for a module declaration are collected in an
`org.myire.scent.metrics.ModuleDeclarationMetrics` instance. These metrics contain the number of
different module directives (requires, exports, provides, uses, and opens) and the comments
associated with the module declaration.

### Package

Scent creates an instance of `org.myire.scent.metrics.PackageMetrics` for each unique package
declared by the ordinary compilation units passed to `JavaMetricsCollector::collect`. These
`PackageMetrics` instances are returned by `JavaMetrics::getPackages` and each of them contains
metrics for the ordinary compilation units that declare the package, as well as metrics for any
comments associated with the package itself (collected from a `package-info.java` file).

### Compilation Unit

The metrics for an ordinary compilation unit are collected in an
`org.myire.scent.metrics.CompilationUnitMetrics` instance. It contains metrics for the type(s)
declared within the compilation unit, and for the comments not associated with these types. These
comments are basically all comments positioned before, between, and after the type declaration(s),
e.g. a file header or comments for import declarations.

Example:

    /*
     * File header comment, collected for the compilation unit.
     */
    package org.myire.scent;
    
    // Comment for the import, collected for the compilation unit.
    import java.util.Map;

    /** JavaDoc for the type, collected for type, not for the the compilation unit. */
    class SomeClass {
      ...
    }

    // Comment after the type declarations, collected for the compilation unit.

### Type

The metrics for a type contain the comments associated with it as well as metrics for its members,
i.e. its fields, methods and inner types. Type metrics are collected in
`org.myire.scent.metrics.TypeMetrics` instances.

Type metrics also hold information about the type's kind, which is one of

* class
* interface
* enum
* enum constant with a class body, which effectively is a subclass of the enclosing enum
* annotation
* anonymous class          

### Method

Metrics for a method contain the metrics for the method's statements and for the comments associated
both with the method itself and with its statements. Method metrics also contain metrics for any
local types declared in the method, e.g. anonymous classes.

Example:

    /** Method JavaDoc, collected for the method */
    int someMethod(int x)
    {
        // Comment for a statement, collected for the method.
        System.out.println(x);

        return x+1;
        // Comment inside the method not belonging to any statement, collected for the method.
    }

Method metrics are collected in `org.myire.scent.metrics.MethodMetrics` instances, which also hold
information about the method's kind, which is one of

* a constructor in a class or an enum
* a non-static initializer in a class or an enum
* a static initializer in a class or an enum
* a non-static method in a class or an enum
* a static method in a class, interface or an enum
* an abstract method in a class, interface, or enum
* a default method in an interface
* a native method in a class or an enum

### Field

Scent collects comment metrics for fields in `org.myire.scent.metrics.FieldMetrics` instances. If
the field is initialized in its declaration, that initialization is collected in the field's
statement metrics.

This field declaration has one comment and no statement:

    // A field declaration without an initalization statement.
    int f;


whereas this field declaration has one comment and one statement:

    // A field declaration with an initalization statement.
    int f = 1;

If a field declaration has multiple variable declarators then each declarator gets its own
`FieldMetrics` instance. Comments associated with the field type are transferred to the first
variable declarator.

This field declaration has four variable declarators, of which the second and third have an
initialization statement:

    // The comment for the field type gets transferred to the FieldMetrics
    // of the first variable declarator
    int
       // This field has no statement
       a,
       // This field has a statement
       b = 17,
       // This field also has a statement
       c = 4711,
       // But this field does not
       d;

In the above example, the field metrics for `a` will have three line comments (two from the type and
one from its declarator), and the field metrics for the other three variable declarators will have
one line comment each.

Field metrics also hold information about the field's kind, which is one of

* a static field in a class, interface, enum or annotation
* a non-static field in a class or an enum.
* an enum constant without a class body, which effectively is a static field
* an annotation type element

### Statements

Scent collects statement metrics by counting the number of statements. As noted above, a field
initialization counts as one statement.

The sub-sections below give examples of all other code elements that count as statements.

#### Assert

An `assert` counts as one statement:

    assert x != null; 

#### Break

A `break` in a loop or a switch counts as one statement:

    for (int i=0; i<10; i++)
    {
        if (someCondition)
            break;
        ...
    }

#### Continue

A `continue` in a loop counts as one statement:

    for (int i=0; i<10; i++)
    {
        if (someCondition)
            continue;
        ...
    }

#### Do

A `do` loop counts as one statement. Statements in the loop's body are counted separately.

    do
    {
        // Statements in loop body
        ...
    }
    while (x > 0);

#### Constructor Invocation

An explicit constructor invocation counts as one statement:

    SomeClass()
    {
        this(10);
    }

    SomeClass(int x)
    {
        super(x);
    }

The implicit invocation of `super()` or the instance initializer in constructors that don't have an
explicit constructor invocation is *not* counted as a statement.

#### Expression Statement

A statement that is an expression is counted:

    x++;

#### For

`for` and `foreach` loops count as one statement. Statements in the loop's body are counted
separately.

    for (int i=0; i<10; i++)
    {
        // Statements in loop body
        ...
    }

    for (Object k : System.getProperties().keySet())
    {
        // Statements in loop body
        ...
    }

#### If

`if-then` and `if-then-else` both count as one statement. Statements in the branch(es) are counted
separately.

    if (someCondition)
    {
        // Statements in branch
        ...
    }
    else
    {
        // Statements in branch
        ...
    }

Note that the `else` itself is considered part of the `if` and does not count as a statement of its
own.

#### Return

A `return` counts as one statement:

    return null;

#### Switch

A `switch` counts as one statement, and each `case` counts as an additional statement. The
statements following a `case` label are counted separately.

    // The switch is one statement
    switch (x)
    {
        // The case label is one statement
        case 0:
            // Any statement after the case label is counted separately
            System.out.println("Unlikely");
        // The case label after a fall-through is one statement
        case 1:
            // Any statement after the case label is counted separately
            System.out.println("Fall-through");
            // The break statement is one statement
            break;
        // The default label is one statement
        default: 
            // Any statement after the default label is counted separately
            System.out.println("Standard");
    }

#### Synchronized

A `synchronized` block counts as one statement. The statements in the block are counted separately.

    synchronized(mutex)
    {
        // Statements in block
        ....
    }

#### Throw

A `throw` counts as one statement:

    throw new RuntimeException();

#### Try

`try-catch`, `try-finally`, `try-catch-finally`, and `try-with` all count as one statement.
Statements in each block are counted separately.

    try
    {
         // Statements in block
         ....
    }
    catch (IOException ioe)
    {
         // Statements in block
         ....
    }
    finally
    {
         // Statements in block
         ....
    }

Note that the `catch` itself is considered part of the `try` and does not count as a statement of
its own. The same goes for the `finally`.

Assignments in a `try-with` are counted as separate statements:

    // The try is one statement and the assignments count as two statements 
    try (InputStream is = new FileInputStream("in"); OutputStream os = new FileOutputStream("out"))
    {
         // Statements in block
         ....
    }

#### While

A `while` loop counts as one statement. Statements in the loop's body are counted separately.

    while (x > 0)
    {
        // Statements in loop body
        ...
    }

#### Not a Statement

A block does not count as a statement, only the statements within the block are counted:

    // The assignment is a statement.
    x = 1;

    // This block is not a statement on its own.
    {
        // Statements in block
        ....
    }

An empty statement does not count as a statement:

    // The assignment is a statement but the extra semicolon doesn't count.
    x = 1;;

An label does not count as a statement:

    // This label does not count as a statement 
    outer:
    // The for loop and its statements are counted as if the label didn't exist
    for (int i=0; i<v2.length; i++)
    {
        int j=i;
        while (j-- != 0)
        {
            if (v1[j] != v2[i])
                continue outer;
        } 
    }

### Comments

Metrics for compilation units, types, methods and fields can all contain metrics for the comments
associated with the code element in question. These metrics are separated into block comments, line
comments, and JavaDocs.

Some examples:

    /**
     * JavaDoc comment for a class. The comment has three lines.
     */
    class SomeClass
    {
        // Two line comments
        // for the field
        int f;

        /** JavaDoc comment for a field, this comment has only one line. */
        Object o;

        /* Multiple block comments */
        /* for a method */ /* a total of three block comments,
                              the first two have one line each
                              and the third has three lines. */
        void someMethod()
        {
            // Line comment for a statement, will be added to the method's comments.
            f++;

            /* A block comment that is not associated with a statement, will also
               be added to the method's comments. */
        }

        /**
         * JavaDoc for inner class.
         */
         static class InnerClass
         {
         }
    }

### Aggregated Metrics

The different metrics classes described above contain metrics for a code element, such as a type or
a method. The individual values in an instance are the values for the corresponding code element,
and does not include the values from any children of the code element. For example, the comment
metrics for a type are collected from the comments that are associated with the type itself. Any
comment metrics from the type's members are not included.

    /** A class. */
    class SomeClass
    {
        // A field
        int f = 2;

        /* Block comments */
        /* for a method */
        void someMethod()
        {
            // Line comment for a statement.
            f++;
        }
    }
 
In the example above, the metrics for the class `SomeClass` will only contain one JavaDoc comment.
The comments and statements from its fields and methods will not be included in the type metrics,
they can only be retrieved by drilling down to the field metrics and method metrics for the type's
members.

To get an aggregation of a code element's metrics and all of its children's metrics, the class
`org.myire.scent.metrics.AggregatedMetrics` comes in handy.

To get an aggregation of a type's metrics and all metrics from its members:

    TypeMetrics typeMetrics = ...
    AggregatedMetrics aggregation = AggregatedMetrics.of(typeMetrics);

If the aggregation shouldn't contain the metrics from the type but only from its members, use
`AggregatedMetrics::ofChildren` instead:

    TypeMetrics typeMetrics = ...
    AggregatedMetrics aggregation = AggregatedMetrics.ofChildren(typeMetrics);


## Dependencies

Scent uses the terrific [JavaParser](http://javaparser.org) to parse Java source code. The jar file
`javaparser-core` is the only runtime dependency that Scent has.

The current version of Scent is compiled and tested with version 3.6.24 of `javaparser-core`. Any
version >= 3.6.18, where support for Java 11 was finalized, will most likely work equally well.


## Differences with JavaNCSS

As noted above, Scent was created to replace JavaNCSS. The main reason was the lack of support for
Java 1.8 source code. Another reason was that JavaNCSS hasn't received an update since 2014, and
seems to be more or less abandoned after its evacuation from Codehaus.

However, Scent does not calculate metrics in exactly the same way as JavaNCSS does. This is
partly because JavaNCSS has some short-comings that are addressed by Scent, and partly because the
two tools have different views on what a statement is.
 
### Compilation Units and Types

JavaNCSS reports the number of compilation units as "classes" in a package. Types are reported as
"objects", but only top-level types in the compilation units are reported.

Inner types are reported as "classes" within an object. Local classes in methods are also reported
in that property, but anonymous classes are not.

JavaNCSS ignores annotations, Scent counts them as types.

If the number of objects and classes in objects reported by JavaNCSS differ from the number of types
reported by Scent, the reason is most likely that Scent counts anonymous classes and annotations as
types and that JavaNCSS does not.

### Methods

JavaNCSS reports methods as "functions", but does not count initializers. This means that

    static {
       System.out.println("Inside static initalizer");
    }
    
    {
       System.out.println("Inside initalizer");
    }

will not be counted as functions by JavaNCSS. Scent, on the other hand, will count the above as two
methods.

### Fields

JavaNCSS does not report metrics on fields.

### Comments

Scent and JavaNCSS agree on comment metrics to a large extent. One difference is that JavaNCSS seems
to count JavaDoc comments for an inner enum in a top-level type as block comments.

Example:

    public class TopLevel {
      static enum InnerEnum {
        // Single line comment for enum constant
        ENUM_CONSTANT_1,
        /** JavaDoc for enum constant. */
        ENUM_CONSTANT_2
      }
    }

The JavaDoc comment for `ENUM_CONSTANT_2` is counted as a block comment (implementation comment) by
JavaNCSS.

Another difference is that JavaNCSS associates file header comments with the package, but Scent
associates them with the compilation unit.

### Statements

The major difference between Scent and JavaNCSS is how statements (called non commenting source
statements, ncss, in JavaNCSS) are counted. In general, Scent is more restrictive about what it
counts as a statement. 

#### Type Declarations

JavaNCSS counts type declarations, including inner types, as statements. Scent does not.

#### Method Declarations

JavaNCSS counts method declarations as statements, Scent does not.

#### Field Declarations

JavaNCSS counts a field declaration as one statement, no matter how many variable declarators there
are and no matter if these variable declarators are initialized. Scent on the other hand only counts
a field declaration as a statement if the variable declarator is initialized. If the field
declaration contains multiple variable declarators with initializations, each of them is counted as
a statement by Scent.

Example:

    int a;

is one statement according to JavaNCSS and zero statements according to Scent, whereas

    int a, b, c = 5, d = 6;

is one statement according to JavaNCSS and two statements according to Scent.
