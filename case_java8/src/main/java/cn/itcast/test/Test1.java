package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test1")
public class Test1 {
    /**
     * 创建线程方法1：继承Thread类，重写run方法
     */
    public static void test1() {
        Thread t = new Thread() {
            @Override
            public void run() {
                log.debug("running");
            }
        };
        t.setName("t1");
        t.start();
    }

    /**
     * 创建线程方法2：实现Runnable接口，重写run方法
     */
    public static void test2() {
        Thread t = new Thread(() -> {
            log.debug("running");
        }, "t2");

        t.start();
    }

    public static void main(String[] args) {
        test1();
        test2();
    }
}
