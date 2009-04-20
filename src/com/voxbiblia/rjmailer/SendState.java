package com.voxbiblia.rjmailer;

import java.util.*;

/**
 * Holds information about the status of recipients of a specific message. 
 */
class SendState
{
    private Map<String, RecipientState> recipients = new HashMap<String, RecipientState>();
    private Map<String, List<String>> mxToRecipients = new HashMap<String, List<String>>();
    private Map<String, SendResult> results = new HashMap<String, SendResult>();

    public SendState(Resolver resolver, List recipients)
    {
        for (Object recipient : recipients) {
            String s = (String) recipient;
            List<String> mxes = resolver.resolveMX(AddressUtil.getDomain(s));

            this.recipients.put(s, new RecipientState(mxes));
        }
    }

    public MXData nextMXData()
    {
        if (mxToRecipients.isEmpty()) {
            for (String s : recipients.keySet()) {
                RecipientState rs = recipients.get(s);
                String mx = rs.nextMX();
                if (mx != null) {
                    List<String> l = mxToRecipients.get(mx);
                    if (l == null) {
                        l = new ArrayList<String>();
                        mxToRecipients.put(mx, l);
                    }
                    l.add(s);
                }
            }
            if (mxToRecipients.isEmpty()) {
                return null;
            }
        }
        String mx = mxToRecipients.keySet().iterator().next();
        MXData d = new MXData(mx, mxToRecipients.get(mx));
        mxToRecipients.remove(mx);
        return d;
    }


    public void hardFailure(String email, String mx, String failure)
    {
        recipients.remove(email);

        results.put(email, new RJMException(
                RJMException.ExactCause.SMTP_UNEXPECTED_STATUS,
                "The server returned an error indicating an unrecoverable " +
                        "error").setServer(mx).setEmail(email)
                .setServerLine(failure));
    }

    /**
     * Return a map with recipient addresses as keys and either RJMResult
     * instances for successful deliveries or RJMExceptions in case of failure.
     *
     * @return a result Map
     */
    public Map<String,SendResult> getResults()
    {
        return results;
    }

    public void softFailure(String email, String mx, String failure)
    {
        RecipientState rs = recipients.get(email);
        if (rs.softFailure(failure)) {
            results.put(email,
                    new RJMException(RJMException.ExactCause.ALL_SERVERS_FAILED,
                    "No more mail servers to try")
                    .setServer(mx).setEmail(email));
            recipients.remove(email);
        }
    }

    public void success(String email, String mx, String result)
    {
        recipients.remove(email);
        results.put(email, new RJMResult(mx, result, RJMResult.Status.SENT));
    }


    private static class RecipientState
    {
        private LinkedList<String> mailExchangers;

        private List<String> softFailures = new ArrayList<String>();

        public RecipientState(List<String> mailExchangers)
        {
            this.mailExchangers = new LinkedList<String>(mailExchangers);
        }

        public String nextMX()
        {
            if (mailExchangers.isEmpty()) {
                return null;
            }
            return mailExchangers.removeFirst();
        }

        // returns true if there are no more mailExchangers to try
        public boolean softFailure(String failureMessage)
        {
            softFailures.add(failureMessage);
            return mailExchangers.isEmpty();
        }

        public List<String> getSoftFailures()
        {
            return softFailures;
        }

    }
}
