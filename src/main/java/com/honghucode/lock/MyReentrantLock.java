package com.honghucode.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class MyReentrantLock {

    private AtomicBoolean state = new AtomicBoolean();
    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());
    ThreadLocal<LockHolder> lockHolderThreadLocal = new ThreadLocal<>();
    private Thread ownThread;

    /**
     * 检查可重入
     * @return
     */
    private boolean checkReentrant() {
        LockHolder lockHolder = lockHolderThreadLocal.get();
        if(null == lockHolder) {
            lockHolderThreadLocal.set(new LockHolder());
            return false;
        }
        if(lockHolder.count > 0) {
            lockHolder.count++;
            return true;
        }
        return false;
    }

    /**
     * 可重入次数加1
     * @return
     */
    private void incReentrant() {
        LockHolder lockHolder = lockHolderThreadLocal.get();
        if(null == lockHolder) {
            throw new RuntimeException("");
        }
        lockHolder.count++;
    }



    public void lock() {
        if(checkReentrant()) {
            System.out.println( Thread.currentThread().getName() + "可重入");
            return;
        }
        if (state.compareAndSet(false, true)) {
            System.out.println( Thread.currentThread().getName() + "抢到锁");
            incReentrant();
            ownThread = Thread.currentThread();
            return;
        }
        Node curNode = new Node();
        // 如果没有抢到锁，使用cas加入到链表尾部
        curNode.thread = Thread.currentThread();
        while (true) {
            Node curTail = tail.get();
            if (tail.compareAndSet(curTail, curNode)) {
                curNode.pre = curTail;
                curTail.next = curNode;
                System.out.println( Thread.currentThread().getName() + " 加入到链表");
                break;
            }
        }

        while (true) {
            // 唤醒逻辑
            // head -> a -> b -> c
            if(curNode.pre == head.get() && state.compareAndSet(false, true)) {
                // 断掉连接
                Node curHead = head.get();
                curHead.next = null;
                curNode.pre = null;
                head.set(curNode);
                break;
            }
            LockSupport.park();
        }

        System.out.println(Thread.currentThread().getName() + " 抢到锁");
        incReentrant();
        ownThread = Thread.currentThread();
    }


    public void unlock() {
        if(ownThread != Thread.currentThread()) {
            System.out.println("非持有锁的线程不能解锁！");
        }
        LockHolder lockHolder = lockHolderThreadLocal.get();
        if(lockHolder.count > 0) {
            lockHolder.count--;
            if(lockHolder.count > 0) {
                return;
            }
        }
        if(lockHolder.count < 0 ) {
            throw new RuntimeException("");
        }

        Node curHead = head.get();
        state.set(false);
        Node next = curHead.next;
        if(null != next) {
            LockSupport.unpark(next.thread);
        }
        System.out.println(Thread.currentThread().getName() + " 释放锁");

    }

    class Node {

        Node pre;
        Node next;
        Thread thread;
    }

    class LockHolder {
        int count;
    }
}
