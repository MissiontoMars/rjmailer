package com.voxbiblia.rjmailer;

import java.util.*;

/**
 * Holds information about the status of recipients of a specific message. 
 */
class SendState
{
       private Map recipients = new HashMap();
    private Map mxToRecipients = new HashMap();
    private Map results = new HashMap();

    public SendState(ResolverProxy resolverProxy, List recipients)
    {
        Iterator i = recipients.iterator();
        while (i.hasNext()) {
            String s = (String)i.next();
            this.recipients.put(s, new RecipientState(resolverProxy.resolveMX(s)));
        }
    }

    public MXData nextMXRecipientData()
    {
        if (mxToRecipients.isEmpty()) {
            Iterator i = recipients.keySet().iterator();
            while (i.hasNext()) {
                String recipient = (String)i.next();
                RecipientState rs = (RecipientState)recipients.get(recipient);
                String mx = rs.nextMX();
                if (mx != null) {
                    List l = (List)mxToRecipients.get(mx);
                    if (l == null) {
                        l = new ArrayList();
                        mxToRecipients.put(mx, l);
                    }
                    l.add(recipient);
                }
            }
            if (mxToRecipients.isEmpty()) {
                return null;
            }
        }
        String mx = (String)mxToRecipients.keySet().iterator().next();
        MXData d = new MXData(mx, (List)mxToRecipients.get(mx));
        mxToRecipients.remove(mx);
        return d;
    }


    public void hardFailure(String email, String mx, String failure)
    {
        recipients.remove(email);
        //noinspection ThrowableInstanceNeverThrown
        results.put(email, new SMTPException(failure, mx));
    }

    /**
     * Return a map with recipient addresses as keys and either RJMResult
     * instances for successful deliveries or RJMExceptions in case of failure.
     *
     * @return a result Map
     */
    public Map getResults()
    {
        return results;
    }

    public void softRecipientFailure(String email, String mx, String failure)
    {
        RecipientState rs = (RecipientState)recipients.get(email);
        if (rs.softFailure(failure)) {
            //noinspection ThrowableInstanceNeverThrown
            results.put(email, new SMTPException(failure + " No more mail servers to try", mx));
            recipients.remove(email);
        }
    }

    public void success(String email, String mx, String result)
    {
        recipients.remove(email);
        results.put(email, new RJMResult(mx, result));
    }


    private static class RecipientState
    {
        private LinkedList mailExchangers;

        private List softFailures = new ArrayList();

        public RecipientState(List mailExchangers)
        {
            this.mailExchangers = new LinkedList(mailExchangers);
        }

        public String nextMX()
        {
            return (String)mailExchangers.removeFirst();
        }

        // returns true if there are no more mailExchangers to try
        public boolean softFailure(String failureMessage)
        {
            softFailures.add(failureMessage);
            return mailExchangers.isEmpty();
        }

        public List getSoftFailures()
        {
            return softFailures;
        }

    }
}
