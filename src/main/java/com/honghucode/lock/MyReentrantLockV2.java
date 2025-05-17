package com.honghucode.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class MyReentrantLockV2 {

    private AtomicInteger state = new AtomicInteger();
    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    /**
     * 检查可重入
     * @return
     */
    private boolean checkReentrant() {
        if(state.get() > 0) {
            state.incrementAndGet();
            return true;
        }
        return false;
    }

    public void lock() {
        if(checkReentrant()) {
            System.out.println( Thread.currentThread().getName() + "可重入");
            return;
        }

        Node curNode = new Node();
        if (!state.compareAndSet(0, 1)) {
            // 如果没有抢到锁
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
                if(curNode.pre == head.get() && state.compareAndSet(0, 1)) {
                    // 断掉连接
                    Node curHead = head.get();
                    curHead.next = null;
                    curNode.pre = null;
                    head.set(curNode);
                    break;
                }
                LockSupport.park();
            }
        }
        System.out.println(Thread.currentThread().getName() + " 抢到锁");
    }


    public void unlock() {
        int count = state.get();
        if(count > 0) {
            state.decrementAndGet();
            return;
        }

        Node curHead = head.get();
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
