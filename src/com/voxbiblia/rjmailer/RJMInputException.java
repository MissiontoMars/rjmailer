package com.voxbiblia.rjmailer;

/**
 */
public class RJMInputException
        extends RJMException
{
    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 1L;

	
    public RJMInputException(String msg)
    {
        super(msg);
    }

    public RJMInputException(String message, Throwable t)
    {
        super(message, t);
    }
}
