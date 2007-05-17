package com.voxbiblia.rjmailer.dns;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;

/**
 * Resolves the MX records of a couple of domains.
 */
public class BatchResolver
{
    public static void main(String[] args)
    {
        enableAllLogging();
        Logger log = Logger.getLogger(BatchResolver.class.getName());

        log.finest("testmessage");

        Resolver r = new Resolver("193.14.119.138");
        r.setTimeout(10);
        long before = System.currentTimeMillis();
        List l = r.resolve(new Query("fark.com"));
        for (int i = 0; i < l.size(); i++) {
            MXRecord mx = (MXRecord)l.get(i);
            System.out.println("mx: " + mx.getExchange() + " p: "+ mx.getPreference());
        }
        System.out.println("elapsed time: " + (System.currentTimeMillis() - before) / 1000.0);
    }

    private static void enableAllLogging()
    {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.FINEST);
        Handler[] handlers = root.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(Level.FINEST);
        }
    }


}
