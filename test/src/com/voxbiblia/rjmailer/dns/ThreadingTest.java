package com.voxbiblia.rjmailer.dns;

/**
 * Some code to test threading behaviour
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class ThreadingTest
{
    private static class Waiter extends Thread
    {
        public void run()
        {
            //try {
                System.out.println("Waiter: sleeping");
                //Thread.sleep(1000);
                System.out.println("Waiter: done sleeping");
            //} catch (InterruptedException e) {
            //    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            //}
            System.out.println("Waiter: waiting");
            try {
                synchronized(this) {
                    this.wait();
                }
                System.out.println("Waiter: wait returned");
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        Waiter w = new Waiter();
        w.start();
        Thread.sleep(1000);
        System.out.println("main: sending notify");
        synchronized(w) {
            w.notify();
        }
    }

}
