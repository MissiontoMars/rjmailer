package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;


/**
 * Tests ConversationScheduler
 */
public class ConverstationSchedulerTest
        extends TestCase
{
    public void testPutSorted()
    {
        // TODO revive
        /*
        ConversationScheduler.MyLinkedList l = new ConversationScheduler.MyLinkedList();
        l.add(new ConversationScheduler.SendTask(0, 100L, null));
        l.add(new ConversationScheduler.SendTask(0, 200L, null));
        l.add(new ConversationScheduler.SendTask(0, 300L, null));

        ConversationScheduler.putSorted(l, new ConversationScheduler.SendTask(0, 150L, null));
        assertEquals(4, l.size());
        assertEquals(150L, ((ConversationScheduler.SendTask)l.get(1)).getTime());

        l.remove(1);
        ConversationScheduler.putSorted(l, new ConversationScheduler.SendTask(0, 50L, null));
        assertEquals(50L, ((ConversationScheduler.SendTask)l.get(0)).getTime());

        ConversationScheduler.putSorted(l, new ConversationScheduler.SendTask(0, 400L, null));
        assertEquals(5, l.size());
        assertEquals(400L, ((ConversationScheduler.SendTask)l.get(4)).getTime());
        */
    }
}
