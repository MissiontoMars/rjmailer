package com.voxbiblia.rjmailer.dns;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Thread that handles all writes and re-writes to the TransportService layer.
 *
 * @author Noa Resare (noa@resare.com)  
 */
class ConversationScheduler extends Thread
{
    private static final Logger log = Logger.getLogger(ConversationScheduler.class.getName());

    /**
     * The number of resends of one particular conversation packet, not including the first
     * packet sent.
     */
    private static final int RESENDS = 2;
    /**
     * The interval in seconds between resends
     */
    private static final int RESEND_INTERVAL = 3;

    /**
     * Specifies something that should be done at a specific time.
     */
    static class SendTask
    {
        private long time;
        private ConversationState state;

        public SendTask(long time, ConversationState state)
        {
            this.time = time;
            this.state = state;
        }

        public long getTime()
        {
            return time;
        }

        public ConversationState getState()
        {
            return state;
        }
    }

    private LinkedList tasks = new LinkedList();
    private TransportService transportService;
    private Map queryMap;

    public ConversationScheduler(TransportService transportService, Map queryMap)
    {
        this.transportService = transportService;
        this.queryMap = queryMap;
    }

    public void enqueue(ConversationState state)
    {
        long now = System.currentTimeMillis();
        SendTask[] ts = new SendTask[RESENDS + 1];
        ts[0] = new SendTask(0, state);
        for (int i = 1; i < RESENDS + 1; i++) {
            ts[i] = new SendTask(now + i * RESEND_INTERVAL * 1000, state);
        }
        synchronized(this) {
            tasks.addFirst(ts[0]);
            for (int i = 1; i < RESENDS + 1; i++) {
                putSorted(tasks, ts[i]);
            }
            this.notify();
        }
    }

    public void remove(int id)
    {

        synchronized(this) {
            Iterator i = tasks.iterator();
            while (i.hasNext()) {
                SendTask t = (SendTask)i.next();
                if (t.getState().getId() == id) {
                    i.remove();
                }
            }
        }
    }


    public void run()
    {
        SendTask task;
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized(this) {
                // this synchornized block is not as large as one might
                // think, as both wait() invocations gives up the monitor.
                while (tasks.size() == 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        log.log(Level.WARNING, "Scheduler interrupted", e);
                    }
                }
                task = (SendTask)tasks.getFirst();
                long toWait = task.getTime() - System.currentTimeMillis();
                while (toWait > 0) {
                    try {
                        this.wait(toWait);
                    } catch (InterruptedException e) {
                        log.log(Level.WARNING, "Scheduler interrupted", e);
                    }
                    try {
                        task = (SendTask)tasks.getFirst();
                        toWait = task.getTime() - System.currentTimeMillis();
                    } catch (NoSuchElementException e) {
                        task = null;
                    }
                }
                if (task == null) {
                    // the list went empty in the inner wait loop
                    continue;
                }
                tasks.removeFirst();
            }

            ConversationState state = task.getState();
            try {
                transportService.send(state.getQuery());
            } catch (Throwable t) {
                state.setException(t);
                // since we got an exception, better wake up the calling
                // thread with the bad news.
                queryMap.get(new Integer(state.getId())).notify();
            }
        }
    }


    static void putSorted(LinkedList tasks, SendTask toAdd)
    {
        long time = toAdd.getTime();
        for (int i = 0; i < tasks.size(); i++) {
            SendTask t = (SendTask)tasks.get(i);
            if (time < t.getTime()) {
                tasks.add(i, toAdd);
                return;
            }
        }
        tasks.addLast(toAdd);
    }
}
