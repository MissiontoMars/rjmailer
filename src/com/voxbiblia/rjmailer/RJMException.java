package com.voxbiblia.rjmailer;

/**
 * The root exception of the RJMailer exception hierarchy.
 */
public class RJMException
    extends RuntimeException
{
    public RJMException(String msg)
    {
        super(msg);
    }

    public RJMException(String message, Throwable t)
    {
        super(message, t);
    }

    public RJMException(Throwable t)
    {
        super(t);
    }
}
