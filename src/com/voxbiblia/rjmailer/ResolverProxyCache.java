package com.voxbiblia.rjmailer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A ResolverProxy that caches its results for a configurable amount of time.
 */
public class ResolverProxyCache
    implements ResolverProxy
{
    private ResolverProxy inner;
    private int minutes;
    private Map<String, List<String>> cacheMap;
    private LinkedList<CacheEntry> entries;

    public ResolverProxyCache(ResolverProxy inner, int minutes)
    {
        this.inner = inner;
        this.minutes = minutes;
        cacheMap = new HashMap<String,List<String>>();
        entries = new LinkedList<CacheEntry>();
    }

    public List<String> resolveMX(String name)
    {
        purgeOldEntries();
        List<String> l = cacheMap.get(name);
        if (l == null) {
            l = inner.resolveMX(name);
            cacheMap.put(name, l);
            entries.add(new CacheEntry(name));
        }
        return l;
    }

    private void purgeOldEntries()
    {
        CacheEntry ce = entries.getFirst();
        while (ce != null) {
            long l = System.currentTimeMillis() - minutes * 60 * 1000;
            if (ce.getCreated() > l) {
                break;
            }

            cacheMap.remove(ce.getKey());
            entries.removeFirst();
            ce = entries.getFirst();
        }
    }

    private static class CacheEntry
    {
        private long created;
        private String key;

        public CacheEntry(String key)
        {
            created = System.currentTimeMillis();
            this.key = key;
        }

        public long getCreated()
        {
            return created;
        }

        public String getKey()
        {
            return key;
        }

    }
}
