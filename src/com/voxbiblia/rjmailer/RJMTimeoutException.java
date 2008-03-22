package com.voxbiblia.rjmailer;

/**
 * 
 */
public class RJMTimeoutException
    extends RJMException
{
    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 1L;
	
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
