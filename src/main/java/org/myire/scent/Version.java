/*
 * Copyright 2019 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.annotation.CheckForNull;


/**
 * Utility methods for getting the Scent library version info from either the module descriptor, if
 * available, or from the {@code java.lang.Package}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
final class Version
{
    // Use reflection to access the methods related to module descriptors to support running on
    // Java versions < 9.
    static private final Method GET_MODULE;
    static private final Method GET_MODULE_DESCRIPTOR;
    static private final Method RAW_VERSION;


    static
    {
        Method aGetModuleMethod = null, aGetModuleDescriptorMethod = null, aRawVersionMethod = null;
        try
        {
            // Get the java.lang.Class:getModule method.
            aGetModuleMethod = Class.class.getMethod("getModule");

            // Get the java.lang.Module::getDescriptor method.
            Class<?> aModuleClass = Class.forName("java.lang.Module");
            aGetModuleDescriptorMethod = aModuleClass.getMethod("getDescriptor");

            // Get the java.lang.module.ModuleDescriptor::rawVersion method.
            Class<?> aModuleDescriptorClass = Class.forName("java.lang.module.ModuleDescriptor");
            aRawVersionMethod = aModuleDescriptorClass.getMethod("rawVersion");
        }
        catch (Exception ignore)
        {
            // Exceptions expected on Java versions < 9.
        }

        GET_MODULE = aGetModuleMethod;
        GET_MODULE_DESCRIPTOR = aGetModuleDescriptorMethod;
        RAW_VERSION = aRawVersionMethod;
    }


    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private Version()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Get the the Scent library's version string.
     *
     * @return  The version string, or null if not available.
     */
    @CheckForNull
    static String getVersionString()
    {
        String aModuleVersion = getModuleVersionString();
        if (aModuleVersion != null)
            return aModuleVersion;
        else
            return Version.class.getPackage().getImplementationVersion();
    }


    /**
     * Get the version string from the module this class is a member of, if any.
     *
     * @return  The version string, or null if the class isn't a member of a module, or if the
     *          module has no version string, or if the Java version doesn't support modules.
     */
    @CheckForNull
    @SuppressWarnings("unchecked")
    static private String getModuleVersionString()
    {
        if (GET_MODULE == null || RAW_VERSION == null)
            return null;

        try
        {
            // This is the reflective equivalent of
            // Module aModule = Version.class.getModule();
            Object aModule = GET_MODULE.invoke(Version.class);
            if (aModule == null)
                return null;

            // This is the reflective equivalent of
            // ModuleDescriptor aDescriptor = aModule.getDescriptor();
            Object aDescriptor = GET_MODULE_DESCRIPTOR.invoke(aModule);
            if (aDescriptor == null)
                return null;

            // This is the reflective equivalent of
            // aDescriptor.rawVersion().orElse(null);
            Object aRawVersion = RAW_VERSION.invoke(aDescriptor);
            if (aRawVersion instanceof Optional<?>)
                return ((Optional<String>) aRawVersion).orElse(null);
            else
                return null;
        }
        catch (Exception ignore)
        {
            return null;
        }
    }
}
