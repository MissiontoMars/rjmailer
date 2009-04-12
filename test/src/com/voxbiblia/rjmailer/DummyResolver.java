package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * For testing the ResolverProxy.
 */
public class DummyResolver
    implements Resolver
{
    private Map responses;

    public DummyResolver(Map responses)
    {
        this.responses = responses; 
    }

    public List resolveMX(String name)
    {
        Object o = responses.get(name);
        List l = new ArrayList();
        if (o instanceof String) {
            l.add(responses.get(name));
            return l;
        } else {
            String[] sa = (String[])o;
            for (int i = 0; i < sa.length; i++ ) {
                l.add(sa[i]);
            }
        }
        return l;
    }
}
