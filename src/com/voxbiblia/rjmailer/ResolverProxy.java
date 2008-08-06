package com.voxbiblia.rjmailer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an interface to the jresolver library without actually
 * requiring the the classes directly, using reflection. 
 */
class ResolverProxy
{
    Object resolver;
    Class resolverClass;
    Class mxQueryClass;
    Class mxRecord;
    Method resolve;
    Method getExchange;

    /**
     * Checks if the jresolver library is found in the classpath.
     *
     * @return true if the jresolver library exists in classpath, else false
     */
    public static boolean hasJresolver()
    {
        try {
            Class.forName("com.voxbiblia.jresolver.Resolver");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public ResolverProxy(String server)
    {
        try {
            resolverClass = Class.forName("com.voxbiblia.jresolver.Resolver");
            mxQueryClass = Class.forName("com.voxbiblia.jresolver.MXQuery");
            mxRecord = Class.forName("com.voxbiblia.jresolver.MXRecord");
            resolve = resolverClass.getMethod("resolve", new Class[] {mxQueryClass});
            Constructor cons = resolverClass.getConstructor(new Class[] {String.class});
            resolver = cons.newInstance(new Object[] {server});
            getExchange = mxRecord.getMethod("getExchange", null);
        } catch (ClassNotFoundException e) {
            throw new Error("Don't try to use the ResolverProxy without " +
                    "having jresolver in your classpath. Get it from " +
                    "http://fs.voxbiblia.com/jresolver", e);
        } catch (Exception e) {
            // there is a boatload of exceptions when using reflection, just
            // propagate.
            throw new Error(e);
        }
    }

    /**
     * Resolves the MX records of the given name, and returns a list of Strings
     * containing mail servers to send email to for the given domain. The list
     * returned is sorted so that lower preference occurs before servers with
     * higher preference. 
     *
     * @param name the hostname to resolve
     * @return a list of mail exchanger hostnames
     */
    public List resolveMX(String name)
    {
        try {
            List l = (List)resolve.invoke(resolver,
                    new Object[] {getMXQuery(name)});
            List result = new ArrayList(l.size());
            for (int i = 0; i < l.size(); i++) {
                result.add(convertMXRecord(l.get(i)));
            }
            return result;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    Object getMXQuery(String name)
    {
        try {
            return mxQueryClass.getConstructor(new Class[] {String.class})
                    .newInstance(new Object[] {name});
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    String convertMXRecord(Object o)
    {
        try {
            return (String)getExchange.invoke(o, null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
