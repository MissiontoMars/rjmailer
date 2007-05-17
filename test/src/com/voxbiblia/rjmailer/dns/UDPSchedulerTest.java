package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;

import java.util.LinkedList;



/**
 * Tests UDPScheduler
 */
public class UDPSchedulerTest
    extends TestCase
{
    public void testPutSorted()
    {
        LinkedList l = new LinkedList();
        l.add(new UDPScheduler.Task(0, 100L, null));
        l.add(new UDPScheduler.Task(0, 200L, null));
        l.add(new UDPScheduler.Task(0, 300L, null));

        UDPScheduler.putSorted(l, new UDPScheduler.Task(0, 150L, null));
        assertEquals(4, l.size());
        assertEquals(150L, ((UDPScheduler.Task)l.get(1)).getTime());

        l.remove(1);
        UDPScheduler.putSorted(l, new UDPScheduler.Task(0, 50L, null));
        assertEquals(50L, ((UDPScheduler.Task)l.get(0)).getTime());

        UDPScheduler.putSorted(l, new UDPScheduler.Task(0, 400L, null));
        assertEquals(5, l.size());
        assertEquals(400L, ((UDPScheduler.Task)l.get(4)).getTime());
    }
}
