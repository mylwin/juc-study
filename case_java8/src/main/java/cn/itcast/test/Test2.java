package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.Test2")
public class Test2 {
    /**
     * 创建线程方法3：实现Callable接口，配合FutureTask获取任务结果
     * @param args
     */
    public static void main(String[] args) {
        // 创建FutureTask对象
        FutureTask<Integer> task = new FutureTask<>(() -> {
            log.debug("running...");
            Thread.sleep(2000);
            return 100;
        });

        // 创建线程
        Thread t1 = new Thread(task, "t1");
        t1.start();

        // 主线程阻塞，同步等待task执行完毕的结果
        try {
            Integer result = task.get();
            log.debug("result: {}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
