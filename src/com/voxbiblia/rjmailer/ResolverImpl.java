package com.voxbiblia.rjmailer;

import com.voxbiblia.jresolver.*;

import java.util.*;

/**
 * Resolves MX records from the DNS system with some short term caching. The
 * number of minutes to cache resolved data is a tradeoff between possibly
 * getting stale data and improved performance.
 *
 */
class ResolverImpl
    implements Resolver
{
    private com.voxbiblia.jresolver.Resolver resolver;
    private int cacheMinutes;

    private Map<String, List<String>> cacheMap =
            new HashMap<String, List<String>>();
    private LinkedList<CacheEntry> entries = new LinkedList<CacheEntry>();


    public ResolverImpl(String server, int cacheMinutes)
    {
        resolver = new com.voxbiblia.jresolver.Resolver(server);
        this.cacheMinutes = cacheMinutes;
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
    public List<String> resolveMX(String name)
    {
        List<String> l;
        synchronized (this) {
            purgeOldEntries();
            l = cacheMap.get(name);
        }
        if (l != null) {
            return l;
        } else {
            l = doResolveMX(name);
            if (l != null) {
                synchronized (this) {
                    cacheMap.put(name, l);
                    entries.add(new CacheEntry(name));
                }
            }
        }
        return l;
    }

    private void purgeOldEntries()
    {
        if (entries.isEmpty()) {
            return;
        }
        CacheEntry ce = entries.getFirst();
        while (ce != null) {
            long l = System.currentTimeMillis() - cacheMinutes * 60 * 1000;
            if (ce.getCreated() > l) {
                break;
            }

            cacheMap.remove(ce.getKey());
            entries.removeFirst();
            ce = entries.getFirst();
        }
    }


    @SuppressWarnings({"unchecked"})
    private List<String> doResolveMX(String name)
    {
        MXQuery query = new MXQuery(name);
        
        try {
            List<MXRecord> l = (List<MXRecord>)resolver.resolve(query);
            Collections.sort(l);
            if (l.isEmpty()) {
                throw new RJMException(ExactCause.DOMAIN_INVALID,
                        "The domain is not set up to receive email, no MX records")
                        .setDomain(name);
            }


            List<String> result = new ArrayList<String>(l.size());
            for (MXRecord r : l) {
                result.add(r.getExchange());
            }
            return result;
        } catch (ServFailException e) {
            throw new RJMException(ExactCause.DOMAIN_FAILURE,
                    "The name server failed to resolve the domain. " +
                            "This failure can be temporary or permanent.")
                    .setDomain(name);
        } catch (TimeoutException e) {
            throw new RJMException(ExactCause.DOMAIN_FAILURE,
                    "The name resolution took too long to perform")
                    .setDomain(name);
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
