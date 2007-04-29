package com.voxbiblia.rjmailer.dns;

/**
 * Prints bitfields as binary strings, most significant bit first
 */
public class BitFieldPrinter
{
    public static void main(String[] args)
    {
        printByte(19);
        printByte(8);
        printByte(129);
    }

    private static void printByte(int b)
    {
        byte b0 = (byte)b;
        System.out.println("01234567");
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            sb.append(((b0 >> i) & 1) != 0 ? "1" : "0");
        }
        System.out.println(sb.toString());
        System.out.println("decimal: " + b0);
    }
}
