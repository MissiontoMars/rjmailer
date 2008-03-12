package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests TextEncoder
 */
public class TextEncoderTest
        extends TestCase
{

    public void testEncode()
            throws Exception
    {
        assertEquals("foo", TextEncoder.encodeQP("foo", "ISO-8859-1"));
        assertEquals("l=E5da", TextEncoder.encodeQP("låda", "ISO-8859-1"));
        assertEquals("a=3Db", TextEncoder.encodeQP("a=b", "ISO-8859-1"));
        assertEquals("=E2=82=AC", TextEncoder.encodeQP("€", "UTF-8"));
    }

    public void testQPLongLine()
            throws Exception
    {
        String someChars = "VILKET ÄR ETT PRÅBLÄM NÄR MAN FÖRSÖKER SJUNGA PÅ ENGELSKA!" +
                "123456789012345678901234567890";
        String s = TextEncoder.encodeQP(someChars, "UTF-8");
        String[] lines =  s.split("\r\n");

        assertTrue("Long QP encoded lines must be broken into shorter lines: "
                + lines.length, lines.length > 1);
        assertTrue("Longer than 78 chars is not allowed: " + lines[0].length(),
                lines[0].length() < 79);
        someChars = "VILKET ÄR ETT PRÅBLÄM NÄR MAN \r\nFÖRSÖKER SJUNGA PÅ ENGELSKA!" +
                "123456789012345678901234567890";
        s = TextEncoder.encodeQP(someChars, "UTF-8");
        lines =  s.split("\r\n");
        assertTrue("not detecting existing newlines", lines[1].length() > 30);
    }

    public void testQPCornerLongLine()
            throws Exception
    {
        String s = "12345678901234567890" + "12345678901234567890" +
                   "12345678901234567890" + "123456789012345678";
        String sOut = TextEncoder.encodeQP(s, "UTF-8");
        assertEquals(sOut,s);
        String s1 = s + "9";
        sOut = TextEncoder.encodeQP(s1, "UTF-8");
        String[] lines = sOut.split("\r\n");
        assertEquals(2, lines.length);
        s = s + "€";
        sOut = TextEncoder.encodeQP(s, "UTF-8");
        lines = sOut.split("\r\n");
        assertTrue("first line is too long: " + lines[0], lines[0].length() < 79);
    }



    public void testCanonicalize()
    {
        assertEquals("before\r\nafter",
                TextEncoder.canonicalize("before\nafter"));
        assertEquals("before\r\nafter\r\nanother",
                TextEncoder.canonicalize("before\nafter\ranother"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\n\nnewlines"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\r\rnewlines"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\r\n\rnewlines"));
    }
    

    public void testEncodeHeaderWord()
    {
        assertEquals("=?ISO-8859-1?B?5eT2xcTW?=",
                TextEncoder.encodeHeaderWord("åäöÅÄÖ", 80));
        assertEquals("=?ISO-8859-1?Q?Gr=F6t?=",
                TextEncoder.encodeHeaderWord("Gröt", 80));
        assertEquals("=?ISO-8859-1?Q?Egon_sover_l=E4nge?=",
                TextEncoder.encodeHeaderWord("Egon sover länge", 80));
        assertEquals("=?UTF-8?Q?Lots_of_=E2=82=AC!?=",
                TextEncoder.encodeHeaderWord("Lots of €!", 80));
    }

    private static final String NEEDS_UTF8 = "На берегу пустынных волн Стоял " +
            "он, дум великих полн";

    public void testEncodedHeaderWordWrap()
    {
        String s = TextEncoder.encodeHeaderWord(NEEDS_UTF8, 20);
        int firstLineLen = s.indexOf("\r\n");
        assertTrue(s, firstLineLen < 21);
        int secondLineLen = s.indexOf("\r\n", firstLineLen + 1) - firstLineLen - 2;
        assertTrue(s, secondLineLen < 79);
    }


    public void testEncodeHeader()
    {
        assertEquals("Subject: subjekt\r\n",
                TextEncoder.encodeHeader("Subject", "subjekt"));
        try {
            TextEncoder.encodeHeader("Subject with colon:", "");
            fail("should have thrown illegal argument exception");
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    public void testEncoderHeaderWrap()
    {
        String s = TextEncoder.encodeHeader("Subject", "this is a really " +
                "really long long long header with some extra data at the end " +
                "really long long long header with some extra data at the end " +
                "but still not finished");
        assertTrue(s.length() > 78);
        assertEquals(78, s.indexOf("\r\n"));
        assertEquals(158, s.indexOf("\r\n", 80));
    }

    public void testEncoderNonAscii()
            throws Exception
    {
        String s = TextEncoder.encodeHeader("Subject", "detta är Muhammud " +
                "Lövånger som äter glass utan att bländas av sol eller måne");
        int len = s.indexOf("\r\n");
        assertTrue("first line is longer than 78 chars: " + len, len < 79);
        char[] chars = s.toCharArray();
        for (int i = 0 ; i < chars.length; i++) {
             if (chars[i] > 128) {
                 fail("non ascii char in header at pos " + i);
             }
        }
        
    }

    public void testGetNonAsciiPercentage()
    {
        assertEquals(82, TextEncoder.getNonAsciiPercentage(NEEDS_UTF8));
        assertEquals(0, TextEncoder.getNonAsciiPercentage("svenska tecken"));
        assertEquals(25, TextEncoder.getNonAsciiPercentage("låda"));
        assertEquals(100, TextEncoder.getNonAsciiPercentage("åäöÅÄÖ"));
    }

    public void testHowMany()
    {
        int B64 = TextEncoder.BASE_64;
        int QP = TextEncoder.QP;
        assertEquals(6, TextEncoder.howMany("abcdefghij", "UTF-8", 10, B64));
        assertEquals(5, TextEncoder.howMany("åbcdefghij", "UTF-8", 10, B64));
        assertEquals(4, TextEncoder.howMany("åäabcdefgh", "UTF-8", 10, B64));
        assertEquals(2, TextEncoder.howMany("€€€abcdefgh", "UTF-8", 10, B64));
        assertEquals(1, TextEncoder.howMany("€€€abcdefgh", "UTF-8", 7, B64));
        assertEquals(8, TextEncoder.howMany("lånläger", "ISO-8859-1", 12, B64));

        assertEquals(6, TextEncoder.howMany("ånäset", "ISO-8859-1", 10, QP));
        assertEquals(5, TextEncoder.howMany("ånäset", "ISO-8859-1", 9, QP));
        assertEquals(4, TextEncoder.howMany("arlöv", "ISO-8859-1", 6, QP));        
        assertEquals(3, TextEncoder.howMany("arlöv", "ISO-8859-1", 5, QP));
        assertEquals(3, TextEncoder.howMany("arlöv", "ISO-8859-1", 4, QP));
        assertEquals(3, TextEncoder.howMany("arlöv", "ISO-8859-1", 3, QP));
        assertEquals(2, TextEncoder.howMany("arlöv", "ISO-8859-1", 2, QP));
    }
}


