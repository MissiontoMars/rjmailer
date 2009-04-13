package com.voxbiblia.rjmailer;

import java.util.*;

/**
 * For testing the ResolverProxy.
 */
public class DummyResolver
    implements Resolver
{
    private Map<String,List<String>> responses =
            new HashMap<String,List<String>>();


    public void addData(String name, String... mxValues)
    {
        responses.put(name, Arrays.asList(mxValues));
    }

    public List<String> resolveMX(String name)
    {
        return responses.get(name);
    }
}
