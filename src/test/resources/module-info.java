/*
 * A file header.
 */

/* Comments for the import statements belong to the compilation unit. */
import com.with.me;

// A line comment for the module.
// Another line comment for the module.
module org.an.iz
{
    // Requires java.xml
    requires transitive java.sql;
    requires static java.xml;

    /* Exports a few packages, */
    /* three to be more
       precise */
    exports org.an.iz.ation;
    exports org.an.iz.ed;
    exports org.an.iz.er;

    /** A JavaDoc here is a bit unexpected. */
    provides java.sql.Driver with org.an.iz.jdbc.OverDrive;
}

// A trailing line comment that belongs to the compilation unit
