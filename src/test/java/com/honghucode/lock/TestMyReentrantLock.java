package com.honghucode.lock;



import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

public class TestMyReentrantLock {

    MyReentrantLock reentrantLock = new MyReentrantLock();
    //ReentrantLock reentrantLock = new ReentrantLock();

    @Test
    public void testLock() throws Exception {
        int[] n = {1000};

        Thread[] threads = new Thread[10];

        for(int i = 0;i < 10;i++) {
            threads[i] = new Thread(() -> {
                 reentrantLock.lock();
                 for(int j = 0;j < 100;j++) {
                     try {
                         //Thread.sleep(1);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     n[0] --;
                 }
                 reentrantLock.unlock();
             });
            threads[i].start();

        }

        for(Thread thread:threads) {
            thread.join();
        }



        System.out.println(n[0]);
    }

    @Test
    public void testLockReentrent() throws Exception {
        reentrantLock.lock();
        reentrantLock.lock();

        reentrantLock.unlock();
        reentrantLock.unlock();
    }

}
