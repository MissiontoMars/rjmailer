package com.voxbiblia.rjmailer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an interface to the jresolver library without actually
 * requiring the the classes directly, using reflection. 
 */
class ResolverProxyImpl
    implements ResolverProxy
{
    private Object resolver;
    private Class mxQueryClass;
    private Method resolve;
    private Method getExchange;

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

    @SuppressWarnings("unchecked")
    public ResolverProxyImpl(String server)
    {
        try {
            Class resolverClass = Class.forName("com.voxbiblia.jresolver.Resolver");
            mxQueryClass = Class.forName("com.voxbiblia.jresolver.MXQuery");
            Class mxRecord = Class.forName("com.voxbiblia.jresolver.MXRecord");
            resolve = resolverClass.getMethod("resolve", new Class[] {mxQueryClass});
            Constructor cons = resolverClass.getConstructor(new Class[] {String.class});
            resolver = cons.newInstance(server);
            getExchange = mxRecord.getMethod("getExchange");
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
    @SuppressWarnings("unchecked")
    public List<String> resolveMX(String name)
    {
        try {

            List<Object> l = (List<Object>)resolve.invoke(resolver,
                    getMXQuery(name));
            List<String> result = new ArrayList<String>(l.size());
            for (Object o : l) {
                result.add(convertMXRecord(o));
            }

            return result;
        } catch (Exception e) {
            if (e instanceof InvocationTargetException
                    && e.getCause() instanceof RuntimeException) {
                throw new RJMDomainException("Failed to resolve mx " + name,
                        e.getCause());
            }
            throw new Error(e);
        }
    }

    private Object getMXQuery(String name)
    {
        try {
            return mxQueryClass.getConstructor(new Class[] {String.class})
                    .newInstance(name);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private String convertMXRecord(Object o)
    {
        try {
            return (String)getExchange.invoke(o);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
