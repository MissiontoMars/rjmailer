package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Tests AddressUtil
 */
public class AddressUtilTest
        extends TestCase
{
    public void testGetAddress()
    {
        assertEquals("foo@foo.bar", AddressUtil.getAddress("foo@foo.bar"));
        assertEquals("another@test.address",
                AddressUtil.getAddress("Test <another@test.address>"));
        assertEquals("third@test",
                AddressUtil.getAddress("\"Tricky <\" <third@test>"));
        // example from RFC2822 A.1.2
        assertEquals("a@b.c",
                AddressUtil.getAddress("\"Giant; \\\"Big\\\" Box\" <a@b.c>"));
    }

    public void testGetDisplayName()
    {
        assertNull(AddressUtil.getDisplayName("a@b.c"));
        assertEquals("Tor",
                AddressUtil.getDisplayName("\"Tor\" <tor@tor.con>"));
        assertEquals("G; \\\"Big\\\" Box",
                AddressUtil.getDisplayName("\"G; \\\"Big\\\" Box\" <a@b.c>"));
        assertEquals("Harald Blåtand",
                AddressUtil.getDisplayName("\"Harald Blåtand\" <h@b.con>"));
    }

    public void testGetAddresses()
    {
        RJMMessage m = new RJMMessage();
        m.setTo(new String[] {"test0@test.co", "\"Foo Bar\" <test1@test.co>"});
        List l = AddressUtil.getToAddresses(m);
        assertTrue(l.contains("test0@test.co"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.co"));
    }

    public void testGetAddressBcc()
    {
        RJMMessage m = new RJMMessage();
        m.setBcc(Arrays.asList("test0@test.co", "\"Foo Bar\" <test1@test.co>"));
        List l = AddressUtil.getToAddresses(m);
        assertTrue(l.contains("test0@test.co"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.co"));
    }

    public void testGetDomain()
    {
        assertEquals("foo.bar", AddressUtil.getDomain("foo@foo.bar"));
    }

    public void testEncodeAddressHeader()
    {
        List<String> to = Arrays.asList("a@b.c", "d@e.f");
        assertEquals("To: a@b.c, d@e.f\r\n",
                AddressUtil.encodeAddressHeader("To", to));
        assertEquals("From: noa@noa.noa\r\n",
                AddressUtil.encodeAddressHeader("From", "noa@noa.noa"));
        assertEquals("To: \"Tester Testson\" <test@test.con>\r\n",
                AddressUtil.encodeAddressHeader("To", 
                        "\"Tester Testson\" <test@test.con>"));
        assertEquals("To: \"=?ISO-8859-1?Q?Harald_Bl=E5tand?=\" <h@b.con>\r\n",
                AddressUtil.encodeAddressHeader("To",
                        "\"Harald Blåtand\" <h@b.con>"));
    }

    public void testEncodeAddressHeaderLong()
    {
        String  to = "\"En person med ett rikitigt riktigt " +
                "långt display name tralla lalla la\" <efraim@ehud.com>";
        String s = AddressUtil.encodeAddressHeader("To", to);
        String[] lines = s.split("\r\n");
        for (int i = 0 ; i < lines.length; i++) {
            assertTrue("i: "+ i + ", " + lines[i].length(), lines[i].length() < 79);
        }

        to = "\"BWO är ett band som består av tre stycken äggmökar, " +
                "varav en blondlockig filur är frontkille och händelsevis" +
                "sångare. Det är ju bra, eftersom man knappast får några" +
                "tonårstjejsbeundrare genom att ställa fram den lederhosen" +
                    "prydda toalettborsten Alexander Bardval vid micken." +
                "Tyvärr kan denna blondlockiga äggmök inte ENGELSKA!!!" +
                "VILKET ÄR ETT PRÅBLÄM NÄR MAN FÖRSÖKER SJUNGA PÅ ENGELSKA!" +
                // a really long line, requiring a soft linebreak
                "Uttalet påminner om.... åh, vilket sammanträffande... en " +
                "svensk som försöker prata engelska! Dessutom är låten" +
                "uill (ja, UILL) maj arms by strånginuff to.... ett" +
                "sällan skådat haveri i paraplegisk tysk marschtakt" +
                "kombinerat med isande yl från en sk kör och något som" +
                "jag antar ska vara romantik. Det låter som en BEGRAVNING!" +
                "Det låter som om Frankenstein har DÖDAT SIN BRUD och ska" +
                "klättra upp på Empire State Building för att offra henne" +
                "till Zeus tillsammans med KING KONG!!\" <foo@bar.bar>";
        s = AddressUtil.encodeAddressHeader("To", to);
        lines = s.split("\r\n");
        for (String line : lines) {

            assertTrue(s, line.length() < 79);
        }

    }
}
