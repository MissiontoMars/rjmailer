package com.voxbiblia.rjmailer;

/**
 *
 */
public class SMTPException
    extends RJMException
{

    private String mx;

    public SMTPException(String msg, String mx)
    {
        super(msg);
        this.mx = mx;
    }


    public SMTPException(String message, Throwable t)
    {
        super(message, t);
    }

    public SMTPException(Throwable t)
    {
        super(t);
    }

    public String getMX()
    {
        return mx;
    }

}
