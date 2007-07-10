package com.voxbiblia.rjmailer.dns;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * A transport service that can be used to test the resend implementation
 * in 
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class DummyTransportService implements TransportService
{
    private static final Logger log = Logger.getLogger(DummyTransportService.class.getName());

    private final byte[] answer;
    private final LinkedList ids = new LinkedList();
    private final Map dropped = new HashMap();

    private int mode = 0;
    static final int REPLY = 0;
    static final int DROP_FIRST = 1;

    public DummyTransportService()
    {
        File testDataFile = new File("test/data/answer.bin");
        answer = new byte[(int)testDataFile.length()];
        try {
            FileInputStream fis = new FileInputStream(testDataFile);
            if (fis.read(answer) != answer.length) {
                throw new Error("failed to read the the answer properly");
            }
            fis.close();
        } catch (IOException e) {
            throw new Error(e);
        }

    }


    public void send(byte[] data)
    {

        Integer i = new Integer(Buffer.parseInt16(data));
        log.fine("packet in with id " + i);
        if (mode == DROP_FIRST) {
            Object o = dropped.get(i);
            if (o == null) {
                dropped.put(i, Boolean.TRUE);
                log.info("dropping packet with id " + i);
                return;
            }
        }
        synchronized(ids) {
            ids.add(i);
        }
        synchronized(this) {
            this.notify();
        }
    }

    public int recv(byte[] buffer)
    {

        try {
            synchronized(this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        System.arraycopy(answer, 0, buffer, 0, answer.length);
        int id;
        synchronized(ids) {
            id = ((Integer)ids.removeFirst()).intValue();
        }
        buffer[0] = (byte)(id >> 8 & 0xff);
        buffer[1] = (byte)(id & 0xff);
        log.fine("packet out with id " + id);
        return answer.length;
    }


    public void setMode(int mode)
    {
        this.mode = mode;
    }
}
