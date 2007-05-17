package com.voxbiblia.rjmailer.dns;

import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramSocket;

/**
 * Created by IntelliJ IDEA.
* User: noa
* Date: May 6, 2007
* Time: 10:00:22 PM
* To change this template use File | Settings | File Templates.
*/
class UDPScheduler extends Thread
{
    private static final Logger log = Logger.getLogger(UDPScheduler.class.getName());

    /**
     * The number of resends of one particular UDP packet, not including the first
     * packet send.
     */
    private static final int RESENDS = 2;
    /**
     * The interval in seconds between UDP resends
     */
    private static final int INTERVAL = 3;

    /**
     * Specifies something that should be done at a specific time.
     */
    static class Task
    {
        /** Put packet send and resend Tasks in the queue */
        public static final int ENQUEUE = 1;
        /** Remove all sends with a specific ide from the queue */
        public static final int REMOVE = 2;
        /*** Send an udp packet at a specified time */
        public static final int SEND = 3;

        private int opcode;
        private long time;
        private Object data;

        public Task(int opcode, long time, Object data)
        {
            this.opcode = opcode;
            this.time = time;
            this.data = data;
        }

        public int getOpcode()
        {
            return opcode;
        }

        public long getTime()
        {
            return time;
        }

        public Object getData()
        {
            return data;
        }
    }

    private LinkedList tasks = new LinkedList();
    private DatagramSocket socket;
    private Map queryMap;

    public UDPScheduler(DatagramSocket socket, Map queryMap)
    {
        this.socket = socket;
        this.queryMap = queryMap;
    }

    public void enqueue(UDPState state)
    {
        Task t = new Task(Task.ENQUEUE, 0L, state);
        synchronized(this) {
            tasks.add(t);
            this.notify();
        }
    }

    public void remove(int id)
    {
        Task t = new Task(Task.REMOVE, 0L, new Integer(id));
        synchronized(this) {
            tasks.add(t);
            this.notify();
        }
    }

    public void run()
    {
        Task task;
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
                task = (Task)tasks.getFirst();
                long toWait = task.getTime() - System.currentTimeMillis();
                while (toWait > 0) {
                    try {
                        this.wait(toWait);
                    } catch (InterruptedException e) {
                        log.log(Level.WARNING, "Scheduler interrupted", e);
                    }
                    task = (Task)tasks.getFirst();
                    toWait = task.getTime() - System.currentTimeMillis();
                }
                tasks.removeFirst();
            }
            switch (task.getOpcode()) {
                case Task.ENQUEUE:
                    doEnqueue((UDPState) task.getData());
                    break;
                case Task.REMOVE:
                    doRemove((Integer) task.getData());
                    break;
                case Task.SEND:
                    doSend((UDPState) task.getData());
                    break;
            }
        }

    }

    private void doRemove(Integer queryId)
    {
        int id = queryId.intValue();

        synchronized(this) {
            Iterator i = tasks.iterator();
            while (i.hasNext()) {
                Task t = (Task)i.next();
                if (t.getOpcode() == Task.SEND
                        && ((UDPState)t.getData()).getId() == id) {
                    i.remove();
                }
            }
        }
    }

    private void doEnqueue(UDPState state)
    {
        long now = System.currentTimeMillis();
        Task[] ts = new Task[RESENDS + 1];
        ts[0] = new Task(Task.SEND, 0, state);
        for (int i = 1; i < RESENDS; i++) {
            ts[i] = new Task(Task.SEND, now + i * INTERVAL * 1000, state);
        }
        synchronized(this) {
            tasks.addFirst(ts[0]);
            for (int i = 1; i < RESENDS; i++) {
                putSorted(tasks, ts[i]);
            }
        }

    }

    private void doSend(UDPState state)
    {
        try {
            log.finest("sending packet");
            socket.send(state.getQuery());
        } catch (Throwable t) {
            state.setException(t);
            // since we got an exception, better wake up the calling
            // thread with the bad news.
            queryMap.get(new Integer(state.getId())).notify();
        }
    }

    static void putSorted(LinkedList tasks, Task toAdd)
    {
        long time = toAdd.getTime();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = (Task)tasks.get(i);
            if (time < t.getTime()) {
                tasks.add(i, toAdd);
                return;
            }
        }
        tasks.addLast(toAdd);
    }
}
