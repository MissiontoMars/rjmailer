package com.voxbiblia.rjmailer;

import java.util.*;

/**
 * Holds information about the status of recipients of a specific message. 
 */
class SendState
{
    private LinkedList mxs = new LinkedList();
    private Map emailsPerMx = new HashMap();

    public SendState(List recipients, ResolverProxy resolver)
    {
        Iterator i = recipients.iterator();
        Map mxCache = new HashMap();
        while (i.hasNext()) {
            String e = (String)i.next();
            String domain = AddressUtil.getDomain(e);
            List mxList = (List) mxCache.get(domain);
            if (mxList == null) {
                mxList = resolver.resolveMX(AddressUtil.getDomain(e));
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

    public MXData next()
    {
        if (mxs.size() == 0) {
            return null;
        }
        String mx = (String)mxs.removeFirst();
        MXData d = new MXData();
        d.setServer(mx);
        d.setRecipients((List)emailsPerMx.get(mx));
        return d;
    }

    public void deliveryResult(String mx, String email, int status, String message)
    {

    }

    public List getResults()
    {
        return null;
    }
}
