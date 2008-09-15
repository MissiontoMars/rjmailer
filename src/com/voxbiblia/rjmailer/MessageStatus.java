package com.voxbiblia.rjmailer;

import java.util.*;

/**
 * Holds information about the status of recipients of a specific message. 
 */
class MessageStatus
{
    private ResolverProxy resolverProxy;
    private LinkedList mxs = new LinkedList();
    private Map emailsPerMx = new HashMap();
    private Map mxCache = new HashMap();

    private Map failures = new HashMap();

    // consuming methods
    public String getNextMX()
    {
        if (mxs.isEmpty()) {
            return null;
        }
        return (String)mxs.removeFirst();
    }

    public List getMXRecipients(String mx)
    {
        return (List)emailsPerMx.get(mx);
    }

    public void mxFailure(String mx)
    {
        
    }

    public void hardRecipientFailure(String email)
    {

    }

    public void softRecipientFailure(String email, String mx)
    {
        String domain = AddressUtil.getDomain(email);
        List resolved = (List)mxCache.get(domain);
        int index = resolved.indexOf(mx);
        if (index == -1) {
            throw new Error("internal inconsistency, unknown mx for email: " + email);
        }
        if (resolved.size() <= index + 1) {
            failures.put("email", "soft failure on last mx");
            return;
        }
        String nextMX = (String)resolved.get(index + 1);
        if (mxs.contains(nextMX)) {
            List l = (List)emailsPerMx.get(nextMX);
            l.add(email);
        } else {
            List l = new ArrayList();
            l.add(email);
            emailsPerMx.put(nextMX, l);
            mxs.add(nextMX);
        }
    }

    public void success(List emails, String result, String mx)
    {
        
    }

    // setup method
    public void addRecipients(List recipients)
    {
        Iterator i = recipients.iterator();
        while (i.hasNext()) {
            String e = (String)i.next();
            String domain = AddressUtil.getDomain(e);
            List mxList = (List) mxCache.get(domain);
            if (mxList == null) {
                mxList = resolverProxy.resolveMX(AddressUtil.getDomain(e));
                mxCache.put(domain, mxList);
            }
            String mx = (String)mxList.get(0);
            if (!mxs.contains(mx)) {
                mxs.add(mx);
            }
            List l = (List)emailsPerMx.get(mx);
            if (l == null) {
                l = new ArrayList();
                emailsPerMx.put(mx, l);
            }
            l.add(e);
        }
    }

    public void setResolverProxy(ResolverProxy resolverProxy)
    {
        this.resolverProxy = resolverProxy;
    }
}
