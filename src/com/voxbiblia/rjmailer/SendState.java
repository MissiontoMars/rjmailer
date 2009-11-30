package com.voxbiblia.rjmailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Holds information about the status of recipients of a specific message. 
 */
class SendState
{
    private Map<String, RecipientState> recipients = new HashMap<String, RecipientState>();
    private Map<String, List<String>> mxToRecipients = new HashMap<String, List<String>>();
    private Map<String, SendResult> results = new HashMap<String, SendResult>();
    private static final Logger log = LoggerFactory.getLogger(SendState.class);

    public SendState(String relayServer, List<String> recipients)
    {
        for (String to : recipients) {
            this.recipients.put(to,new RecipientState((relayServer)));
        }
    }

    public SendState(Resolver resolver, List<String> recipients)
    {
        for (String s : recipients) {
            try {
                String domain = AddressUtil.getDomain(s);
                List<String> mxes = resolver.resolveMX(domain);
                if (log.isDebugEnabled()) {
                    for (String mx: mxes) {
                        log.debug("Found mx {} for domain {}", mx, domain);
                    }
                }
                this.recipients.put(s, new RecipientState(mxes));
            } catch (RJMException e) {
                throw e.setEmail(s);
            }
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


    public void hardFailure(String email, RJMException e)
    {
        RecipientState rs = recipients.remove(email);
        results.put(email, e.setSoftFailures(rs.getSoftFailures()));
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


    public void softFailure(String to, RJMException e)
    {
        RecipientState rs = recipients.get(to);

        if (rs.softFailure(e)) {
            results.put(to,
                    new RJMException(ExactCause.ALL_SERVERS_FAILED,
                    "No more mail servers to try")
                    .setEmail(e.getEmail()).setSoftFailures(rs.getSoftFailures()));
            recipients.remove(to);
        }
    }

    public void success(String email, String mx, String result,
                        String tlsCipherSuite, String tlsCertHash)
    {
        RecipientState rs = recipients.remove(email);
        RJMResult r = new RJMResult(mx, result, RJMResult.Status.SENT);
        r.setTlsCipherSuite(tlsCipherSuite);
        r.setTlsCertHash(tlsCertHash);
        List<RJMException> softFailures = rs.getSoftFailures();
        if (softFailures != null) {
            r.setSoftFailures(softFailures);
        }
        results.put(email, r);
    }


    private static class RecipientState
    {
        private LinkedList<String> mailExchangers;

        private List<RJMException> softFailures = null;

        public RecipientState(String mailExchanger)
        {
            this(Collections.singletonList(mailExchanger));
        }


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
        public boolean softFailure(RJMException e)
        {
            if (softFailures == null) {
                softFailures = new ArrayList<RJMException>();
            }
            softFailures.add(e);
            return mailExchangers.isEmpty();
        }

        public List<RJMException> getSoftFailures()
        {
            return softFailures;
        }

    }
}
