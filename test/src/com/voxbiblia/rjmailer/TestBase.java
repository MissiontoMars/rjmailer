package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Common base class of all tests.
 */
public abstract class TestBase
    extends TestCase
{
    private static boolean loggingConfigured = false;

    @Override
    protected void setUp() throws Exception
    {
        setupLogging();
    }

    private static void setupLogging()
    {
        if (!loggingConfigured) {
            Logger root = Logger.getLogger("");
            for (Handler h : root.getHandlers()) {
                h.setLevel(Level.ALL);
            }
            root.setLevel(Level.FINEST);
            loggingConfigured = true;
        }
    }
}
