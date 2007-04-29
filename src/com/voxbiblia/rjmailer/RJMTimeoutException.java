package com.voxbiblia.rjmailer;

/**
 * 
 */
public class RJMTimeoutException
    extends RJMException
{

    public RJMTimeoutException(String msg)
    {
        super(msg);
    }

    public RJMTimeoutException(String message, Throwable t)
    {
        super(message, t);
    }

    public RJMTimeoutException(Throwable t)
    {
        super(t);
    }
}
