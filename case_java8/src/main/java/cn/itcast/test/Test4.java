package cn.itcast.test;

import cn.itcast.Constants;
import cn.itcast.n2.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程的run和start方法
 * 启动线程的正确方式是调用start方法，而不是调用run方法
 * 因为调用run方法只是让线程执行了run方法中的代码，而没有启动线程
 */
@Slf4j(topic = "c.Test4")
public class Test4 {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running...");
                FileReader.read(Constants.MP4_FULL_PATH);
            }
        };

        t1.start();
        log.debug("do other things...");
    }
}
