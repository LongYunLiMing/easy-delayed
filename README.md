# 延时任务

#### 一、使用场景

​	延时任务用于在具体时刻执行“一次性”任务。执行的任务具备“一次性” 即触发后便不能再次被触发。但通过在延时任务触发之后添加一个新的延时任务可以实现“非一次性”任务，只是这种“非一次性”任务并不是真正的实现了任务的连续触发，实际上每次执行的延时任务都是一个新挂起的任务，在数据上表现为多个延时任务。



#### 二、使用指南

  接下来通过预留的一些接口来介绍延时任务的使用



##### 2.1、创建调度器

```java
//创建调度器对象
StandScheduler scheduler = new StandScheduler(thread);
```

   通过构造器创建一个调度器对象，但是创建调度器的时候需要传入一个调度的主线程对象。

```java
//创建调度线程
SchedulerThread thread = new SchedulerThread(new ThreadPoolExecutor(1 , 1 , 0L , TimeUnit.MILLISECONDS , new LinkedBlockingQueue<>()),store);
```

   创建调度线程的时候需要传入一个线程池和延时任务的存储对象，其中延时任务的存储对象目前提供基于内存和 JDBC 存储延时任务的实现类，因此只需要创建对应的实现类即可

```java
//创建基于 JDBC 的延时任务存储对象
DelayedStore store = new JDBCDelayedStore();
//创建基于内存的延时任务存储对象
DelayedStore store = new MemoryDelayedStore();
```



##### 2.2、调度器相关 API 介绍

- 开启延时任务服务 —— 调度器被创建后调度的主线程并没有启动，只有当 start() 方法被调用时主线程才会正式启动

```java
scheduler.start();
```

- 暂停延时任务服务 —— 当 start() 方法被调用后主线程已经开始调度工作，但是如果需要暂停调度任务的话就需要 pause() 方法让主线程间接休眠暂停调度任务，等待恢复调度的信号。

```
scheduler.pause();
```

- 恢复延时任务服务 —— 当 pause() 方法被调用后主线程会进入间接休眠的状态，虽然线程没有结束但也不执行调度任务，只有调用 resume() 方法后才会让主线程继续执行调度任务。

```java
scheduler.resume();
```

- 关闭延时任务服务 —— 和 pause() 方法的区别是，pause() 方法暂停了调度任务之后还可以通过 resume() 方法恢复调度，而 shutdown() 方法则会让调度的主线程直接杀死，不能再次恢复。

```java
scheduler.shutdown();
```

- 调度一个具体的延时任务 —— 该方法会将需要调度的延时任务加入到存储器中，并且会触发主线程重新获取最新的调度任务

```java
scheduler.scheduleJob(Delayed delayed);
```

- 立即触发一个已存在的延时任务 —— 调用该 API 会根据 group 和 code 查找存储器中的延时任务并且得到立即执行

```
scheduler.triggerJob(String group, String code);
```

- 立即触发一个组下的所有延时任务 —— 调用该 API 会根据 group 查找存储器中目标组下的所有延时任务并且得到立即执行

```
scheduler.triggerJob(String group);
```

- 删除一个具体的延时任务 —— 调用该 API 会将目标延时任务从存储器中删除

```java
scheduler.deleteJob(String group, String code);
```

- 删除一个组下的所有延时任务 —— 调用该 API 会将目标组下的所有延时任务从存储器中删除

```java
scheduler.deleteJob(String group, String code);
```

- 暂停一个具体的延时任务 —— 调用该 API 后目标延时任务不会被触发但是延时任务的数据仍然存在，可以通过 API 恢复

```java
scheduler.pauseJob(String group, String code);
```

- 暂停一个组下的所有延时任务 —— 调用该 API 后目标组下的所有延时任务不会被触发但是延时任务的数据仍然存在，可以通过 API 恢复

```java
scheduler.pauseJob(String group);
```

- 恢复一个具体的延时任务 —— 调用该 API 后会将目标延时任务重新加入到调度中

```java
scheduler.resumeJob(String group, String code);
```

- 恢复一个组下的所有延时任务 —— 调用该 API 后会将目标组下的所有延时任务重新加入到调度中

```java
scheduler.resumeJob(String group);
```



##### 2.3、Listener —— 监听器

​	监听器目前支持在 start、halt、pause、resume 四个动作被触发时回调。

- 普通 java 应用中添加监听器 —— 创建出调度器后通过 addListeners（）方法即可实现添加监听器

  ```java
  scheduler.addListeners(List<Listener> listeners);
  ```

- Spring Boot 应用中添加监听器 —— Spring 中只需要将 Listener 的实现类加入到 Spring 容器，在自动装配的过程中便会加入到调度器中



##### 2.4、Plugin —— 插件

  插件会在延时任务被执行的前后调用，因此我们可以通过插件来修改延时任务执行时的上下文对象，从而更加灵活的配合延时任务执行。例如可以在插件中存储延时任务执行的结果以及记录执行日志等。

- 普通 java 程序中添加插件 —— 创建出调度器后通过 addPlugins（）方法即可实现添加插件

```java
scheduler.addPlugins(List<Plugin> plugins);
```

- Spring Boot 应用中添加监听器 —— Spring 中只需要将 Plugin 的实现类加入到 Spring 容器，在自动装配的过程中便会加入到调度器中



##### 2.5、Job —— 任务

​	Job 是具体执行任务的抽象接口，所有的任务都必须继承该接口。

- 普通任务 —— com.bds.easy.delayed.core.Job

  ​	普通的 Job 任务在触发后便会结束，是最简单和最基本的任务类型；使用时只需要继承 Job 接口并在 execute() 方法中编写逻辑代码

  ```java
  public class DemoJob implements Job{
      @Override
      public void execute(JobExecuteContext context){
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
          System.out.println("触发延时任务 —— 当前时间："+sdf.format(new Date()));
      }
  }
  ```

  

- 连续任务 —— com.bds.easy.delayed.job.ContinueJob

  ​	连续任务触发后会在指定时长后再次触发；使用时只需要继承 ContinueJob 抽象类并在 handle() 方法中编写逻辑代码

```java
//连续任务 demo 
public class DemoContinueJob extends ContinueJob{
  	//具体的任务逻辑
    @Override
    public void handle(JobExecuteContext context,Integer count){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("第 "+ count +" 次触发延时任务 —— 当前时间："+sdf.format(new Date()));
    }

    //在一分钟以后再次触发当前任务
    @Override
    public Long spacingTime(){
        return Long.valueOf(1000 * 60);
    }
}
```



##### 2.6、Spring Boot 中使用

  我们已经实现了与 SpingBoot 的集成，通过自动装配便可以实现。

- 开启自动装配 —— 通过注解开启自动装配

```java
@EnabledDelayedAutoConfiguration
```

- 使用调度器 —— 实现自动装配以后就可以在任务容器中通过 @Autowired 注解注入调度器

```java
@Autowired
private Scheduler scheduler;
```

- 通过配置文件调整自动装配过程 

```yaml
easy:
  delayed:
    store: jdbc ## jdbc：使用基于 jdbc 的延时任务存储器；memory：使用基于内存的延时任务
														。。。。。。
```



#### 三、展望

​    目前实现了最基本的功能，只是一个“速成品”没有经过严格和缜密的测试，从代码的可读性和程序运行的稳定性来说都还需要不断的完善。
