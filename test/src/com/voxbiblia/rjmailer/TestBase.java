package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Common base class of all tests.
 */
public abstract class TestBase
    extends TestCase
{
    private static boolean loggingConfigured = false;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
    private static final String lineSep = System.getProperty("line.separator");

    private static class LogFormatter extends Formatter
    {
        public String format(LogRecord logRecord)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(new Date()));
            sb.append(" ");
            String[] classParts = logRecord.getSourceClassName().split("\\.");
            sb.append(classParts[classParts.length - 1]);
            sb.append(" ");
            sb.append(logRecord.getLevel());
            sb.append(" ");
            sb.append(logRecord.getMessage());
            sb.append(lineSep);
            return sb.toString();
        }
    }

    @Override
    protected void setUp() throws Exception
    {
        setupLogging();
    }

    private static void setupLogging()
    {

        if (!loggingConfigured) {
            LogFormatter lf = new LogFormatter();
            Logger root = Logger.getLogger("");
            for (Handler h : root.getHandlers()) {
                h.setLevel(Level.ALL);
                h.setFormatter(lf);
            }
            root.setLevel(Level.FINEST);
            loggingConfigured = true;
        }
    }
}
