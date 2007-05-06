package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Tests Resolver
 */
public class ResolverTest
    extends TestCase
{
 
    public void testResolve()
    {
        Resolver r = new Resolver("192.168.0.1");
        List l = r.resolve(new Query("dn.se"));
        assertEquals(1, l.size());
        assertEquals("mail-gw.dn.se", ((MXRecord)l.get(0)).getExchange());
        assertEquals(10, ((MXRecord)l.get(0)).getPreference());
    }

    public void testParseResponse()
            throws Exception
    {
        File testDataFile = new File("test/data/answer.bin");
        byte[] data = new byte[(int)testDataFile.length()];
        FileInputStream fis = new FileInputStream(testDataFile);
        assertEquals(data.length, fis.read(data));
        fis.close();

        List l = Resolver.parseResponse(data);
        assertNotNull(l);
        assertEquals(3, l.size());

        MXRecord r = (MXRecord)l.get(0);
        assertEquals(5, r.getPreference());
        assertEquals("johanna.resare.com", r.getExchange());
        r = (MXRecord)l.get(1);
        assertEquals(10, r.getPreference());
        assertEquals("evert.evolvator.se", r.getExchange());
        r = (MXRecord)l.get(2);
        assertEquals(20, r.getPreference());
        assertEquals("ulla.resare.com", r.getExchange());
    }

}
