package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 查看线程状态的方式：.getState()
 * 线程的状态：
 * NEW（新建）,
 * RUNNABLE（可运行），
 * BLOCKED（阻塞），
 * WAITING（等待），
 * TIMED_WAITING（超时等待），
 * TERMINATED（终止）
 */
@Slf4j(topic = "c.Test5")
public class Test5 {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };

        System.out.println(t1.getState());
        t1.start();
        System.out.println(t1.getState());
    }
}
