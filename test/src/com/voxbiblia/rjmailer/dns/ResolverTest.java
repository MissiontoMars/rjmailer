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
        r.resolve(new Query("resare.com"));
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

    }

}
