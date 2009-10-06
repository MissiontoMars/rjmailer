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
        if ("servfail-domain".equals(name)) {
            throw new RJMException(ExactCause.DOMAIN_FAILURE,
                    "The resolving nameserver failed to look up name: " + name
                    ).setDomain(name);
        }


        List<String> l =  responses.get(name);
        if (l == null) {
            throw new RJMException(ExactCause.DOMAIN_NOT_FOUND,
                    "No data for domain: "+  name).setDomain(name);
        }
        return l;
    }
}
