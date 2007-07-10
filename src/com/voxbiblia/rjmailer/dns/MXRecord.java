package com.voxbiblia.rjmailer.dns;

/**
 * An MX record
 */
public class MXRecord implements Comparable
{
    private String exchange;
    private int preference;

    public String getExchange()
    {
        return exchange;
    }

    public void setExchange(String exchange)
    {
        this.exchange = exchange;
    }

    public int getPreference()
    {
        return preference;
    }

    public void setPreference(int preference)
    {
        this.preference = preference;
    }

    public int compareTo(Object o)
    {
        return preference - ((MXRecord)o).getPreference();
    }

    public String toString()
    {
        return "{" + getExchange() + ": " + getPreference() + "}";
    }
}
