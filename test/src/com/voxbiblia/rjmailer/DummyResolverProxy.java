package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * For testing the ResolverProxy.
 */
public class DummyResolverProxy
    implements ResolverProxy
{
    private Map responses;

    public DummyResolverProxy(Map responses)
    {
        this.responses = responses; 
    }

    public List resolveMX(String name)
    {
        List l = new ArrayList();
        l.add(responses.get(name));
        return l;
    }
}
