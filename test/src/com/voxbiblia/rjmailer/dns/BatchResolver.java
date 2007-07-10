package com.voxbiblia.rjmailer.dns;

import java.util.List;

/**
 * Resolves the MX records of a couple of domains.
 */
public class BatchResolver
{
    public static void main(String[] args)
    {
        Resolver r = new Resolver("johanna.resare.com");
        long before = System.currentTimeMillis();
        List l = r.resolve(new MXQuery("svd.se"));
        for (int i = 0; i < l.size(); i++) {
            MXRecord mx = (MXRecord)l.get(i);
            System.out.println("mx: " + mx.getExchange() + " p: "+ mx.getPreference());
        }
        System.out.println("elapsed time: " + (System.currentTimeMillis() - before) / 1000.0);
    }
}
