# JUC 并发编程学习代码

Java 并发编程（JUC）课程配套示例代码，按知识点分章组织，并附工具链实验（JMH 基准测试、jcstress 指令重排序验证）与几个综合案例（迷你 Tomcat、内存监控、限流与 Disruptor）。

- **运行环境：JDK 8**（务必，原因见下文「为什么必须用 JDK 8」）
- 8 个**互相独立**的 Maven 模块（没有父 pom，需要分别构建）
- 已验证：JDK 8（Temurin 1.8.0_504）下 8 个模块 `mvn clean compile` 全部成功

## 目录结构

```
juc-concurrent/
├── case_java8/           # 主体：按章节划分的课堂代码（137 个源文件）
│   └── src/main/java/cn/itcast/
│       ├── n2/  n3/  n4/  n5/  n7/  n8/   # 第 2~8 章（n6 为空目录，无代码）
│       ├── pattern/      # 多线程设计模式
│       ├── test/         # 视频中现场敲写的代码（内容与前面章节重复）
│       └── Constants.java
├── case_java7/           # JDK7 HashMap 并发扩容死链演示
├── case_tomcat/          # 迷你 Tomcat：自定义线程池 + ServerSocket + Servlet
├── case_monitor/         # Spring Boot 内存监控（balking + 两阶段终止）
├── case_thirdpart/       # Spring Boot + Guava 限流 / CountDownLatch / Disruptor
├── jmh_performance/      # JMH：多线程分片求和 vs 单线程
├── jmh_eliminate_locks/  # JMH：锁消除（x++ vs 局部对象 synchronized）
├── jcstress_ordering/    # jcstress：指令重排序 / 可见性
└── tmp/                  # TestWordCount 的词频测试数据（1.txt ~ 26.txt，勿删）
```

## 环境要求

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | **8** | 必须。其他版本会出现编译失败或演示失真，见下文 |
| Maven | 3.6+ | 各模块独立构建，无父 pom |
| MySQL | 5.1+（可选） | 仅 `n8` 的 `GenericDao`/`TestGenericDao` 示例需要：`jdbc:mysql://localhost:3306/test`，账号密码 `root/root` |

### 为什么必须用 JDK 8

1. **偏向锁**：JDK 15 起已废弃并移除。`n4/TestBiased.java`、`test/TestBiased.java` 依赖 `-XX:+UseBiasedLocking`、`-XX:BiasedLockingStartupDelay=0`、`-XX:+TraceBiasedLocking` 等参数，在 JDK 17 上 **JVM 会直接启动失败**（Unrecognized VM option），JOL 打印的对象头也不再是偏向锁语义。
2. **lombok 1.18.10** 不支持 JDK 16+ 的注解处理。
3. **`sun.misc.Hashing`** 仅存在于 JDK 7，`case_java7` 的原始代码依赖它。
4. **Spring Boot 2.2.2**（`case_monitor`、`case_thirdpart`）不支持 JDK 17，其 Spring 5.2 / ASM 版本读不了高版本字节码。

## 快速开始

```powershell
# 1) 指向 JDK 8（按自己的安装路径修改）
$env:JAVA_HOME = 'D:\java\jdk-8u504'

# 2) 构建单个模块
mvn -f case_java8/pom.xml clean compile

# 3) 一次构建全部 8 个模块
foreach ($m in 'case_java8','case_java7','case_tomcat','case_monitor','case_thirdpart','jmh_performance','jmh_eliminate_locks','jcstress_ordering') {
    mvn -f "$m/pom.xml" clean compile
}
```

IDEA 中还需确认 `Settings → Build Tools → Maven → Runner → JRE` 也选 JDK 8，否则内嵌 Maven 仍会用默认 JDK 编译。

## 模块说明与运行方式

| 模块 | 内容 | 运行方式 |
|---|---|---|
| `case_java8` | 主体课堂代码，见下方章节索引 | 直接运行各类的 `main` 方法 |
| `case_java7` | HashMap 并发扩容死链（JDK7 经典 bug） | 运行 `test.TestDeadLink#main`（需 JDK 7 才能复现死链） |
| `case_tomcat` | `Jerrymouse`：`ServerSocket` 绑定 `127.0.0.1:80` + `ThreadPoolExecutor(8,16,60s,ArrayBlockingQueue(10))`；`cn.itcast.threadpool` 包内还有手写线程池 | 运行 `cn.itcast.web.Jerrymouse#main`，浏览器访问 http://127.0.0.1/ |
| `case_monitor` | 监控线程每 2 秒采集 JVM 内存写入 `ArrayBlockingQueue(30)`，`/info` 用 `drainTo` 取走，前端 ECharts 绘图 | 运行 `MonitorApplication#main`，访问 http://localhost:8080/ ，页面按钮调用 `/start` `/stop` |
| `case_thirdpart` | Guava `RateLimiter`/`Semaphore` 限流、CountDownLatch 聚合订单/商品/物流接口、Disruptor RingBuffer | 运行 `ThirdpartApplication#main`，接口 `/test`、`/order/{id}`、`/product/{id}`、`/logistics/{id}`；`TestDisruptor` 也有 `main` |
| `jmh_performance` | JMH 基准：4 线程分片求和 vs 单线程 | `mvn clean package` 后 `java -jar target/benchmarks.jar` |
| `jmh_eliminate_locks` | JMH 基准：`x++` vs 局部对象 `synchronized`（演示 JIT 锁消除） | 同上，产物 `target/benchmarks.jar` |
| `jcstress_ordering` | jcstress 测试：`ready`/`num` 指令重排序（结果为 `0` 即观察到了重排序） | `mvn clean package` 后 `java -jar target/jcstress.jar`，报告在 `target/results/index.html` |

### case_java8 章节索引

| 包 | 主题 | 代表类 |
|---|---|---|
| `n2` | 进程与线程、同步与异步调用 | `Sync`、`Async`、`util.Sleeper`、`util.FileReader` |
| `n3` | 线程的创建与方法：`start`/`run`、`sleep`/`yield`/`join`、`interrupt`、守护线程、6 种线程状态、栈帧、多线程分工 | `ThreadStarter`、`TestStart`、`TestInterrupt`、`TestDaemon`、`TestState`、`TestFrames`、`TestMakeTea`（泡茶） |
| `n4` | 共享模型与管程：`synchronized` 八锁、偏向锁、锁消除、`wait`/`notify`、`park`/`unpark`、`ReentrantLock`（可重入/可打断/超时/公平/条件变量）、死锁与活锁与饥饿、CAS、`Unsafe`、售票与转账练习 | `Test8Locks`、`TestBiased`、`TestWaitNotify`、`TestParkUnpark`、`reentrant/*`、`deadlock/v1`、`deadlock/v2`、`deadlock/v3`、`TestLiveLock`、`TestThreadHungry`、`exercise/*`、`TestCorrectPostureStep1~5` |
| `n5` | Java 内存模型：`volatile`、`final`、DCL 单例、指令重排 | `TestVolatile`、`TestFinal`、`Singleton`、`Initialize` |
| `n7` | 无锁并发：原子累加器、`AtomicIntegerArray` 连接池、日期格式化线程安全 | `Test1`、`Test2`、`Test3`、`TestDateParse` |
| `n8` | 并发工具：自定义线程池+阻塞队列+拒绝策略、`Executors`、`submit`/`shutdown`、`ScheduledExecutor`、`ForkJoin`、AQS 自定义锁、读写锁/`StampedLock`、`Semaphore`（限流与连接池）、`CountDownLatch`/`CyclicBarrier`、基于 `AtomicReference` 的并发队列、读写锁缓存 | `TestPool`、`TestExecutors`、`TestForkJoin`、`TestAqs`、`TestReadWriteLock`、`TestStampedLock`、`TestSemaphore`、`TestGenericDao`、`concurrentqueue/v1`、`concurrentqueue/v2`、`TestWordCount` |
| `pattern` | 多线程设计模式：两阶段终止、balking、保护性暂停（v1~v3）、生产者消费者、顺序控制 | `TestTwoPhaseTermination`、`TestBalking`、`TestGuardedObject[V2/V3]`、`TestProducerConsumer`、`TestOrder` |
| `test` | 视频中现场敲写的代码（`Test1`~`Test42` 等），内容与前面章节重复 | — |

日志由 `case_java8/src/main/resources/logback.xml` 控制，只输出 `c.*` 命名空间的 debug 日志。

## 常见问题

1. **`Could not find artifact org.openjdk.jol:jol-core:jar:0.10-TEST`**
   原始 pom 里这个版本号在任何公共仓库都不存在（Maven Central 只有 0.7~0.17），且 `toPrintableSimple(...)` 方法在 0.7~0.17 中均不存在。已修正为 `0.10`，并把演示代码改用 `toPrintable()`。

2. **`Fatal error compiling: 无效的目标发行版: 17`**
   `settings.xml` 里若有 `activeByDefault` 的 profile 设置了 `maven.compiler.source/target`，会**覆盖所有工程 pom 里的编译级别**（Maven 中用户属性优先于 pom 属性）。本仓库已在各 pom 用**插件级** `<configuration>` 固定编译级别，不受该 profile 影响。若其他项目遇到同样问题，检查 `~/.m2/settings.xml` 与 `$MAVEN_HOME/conf/settings.xml`。

3. **`unmappable character for encoding GBK`**
   源码是 UTF-8，而命令行 Maven 默认使用系统编码。各 pom 已加 `project.build.sourceEncoding=UTF-8`。

4. **`TestWordCount` 报找不到 `tmp/1.txt`**
   它使用相对路径读取 `tmp/`，**运行时工作目录必须是本仓库根目录**。

5. **`sun.misc.Unsafe is internal proprietary API` 警告**
   JDK 8 下的正常警告，不影响编译与运行。

6. **`case_java7` 的 TestDeadLink 不再死循环**
   JDK 8 已修复 HashMap 并发扩容死链问题，该演示只在 JDK 7 下才有原有效果。

## 提交规范

提交信息统一使用约定式前缀：`feat: 新增xxx`、`fix: 修复xxx`、`docs: 完善xxx`。

## 声明

本项目为个人学习《Java 并发编程》课程时整理的示例代码，原始课程代码版权归原作者及所属机构所有，仅供学习交流使用。
