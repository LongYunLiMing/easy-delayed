package com.bds.easy.delayed.core;

import com.bds.easy.delayed.store.DelayedException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 15:51
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public class SchedulerThread extends Thread{
    private final Logger LOGGER = LoggerFactory.getLogger(SchedulerThread.class);
    private final Object sigLock;
    private AtomicBoolean halted;
    private AtomicBoolean paused;
    private ThreadPoolExecutor executor;
    private DelayedStore delayedStore;
    private Integer size = 5;
    private Long threshold = 1000L;
    private Scheduler scheduler;
    private List<Listener> listeners;
    private List<Plugin> plugins;

    public SchedulerThread(ThreadPoolExecutor executor, DelayedStore delayedStore){
        super("delayed");
        this.sigLock = new Object();
        this.halted = new AtomicBoolean(true);
        this.paused = new AtomicBoolean(false);
        this.executor = executor;
        this.delayedStore = delayedStore;
    }

    /**
     * 通知监听器
     * @param action
     */
    private void noticeListener(ActionEnum action){
        if(CollectionUtils.isNotEmpty(this.listeners)){
            for (Listener listener : this.listeners){
                switch (action){
                    case HALT:
                        listener.haltAction();
                        break;
                    case START:
                        listener.startAction();
                        break;
                    case PAUSE:
                        listener.pauseAction();
                        break;
                    case RESUME:
                        listener.resumeAction();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public DelayedStore getDelayedStore(){
        return delayedStore;
    }

    public void setSize(Integer size){
        this.size = size;
    }

    public Boolean isHalted(){
        return this.halted.get();
    }

    public Boolean isPaused(){
        return this.paused.get();
    }

    /**
     * 立即触发指定的延时任务
     * @param group
     * @param code
     */
    public void triggerJob(String group , String code) throws SchedulerException{
        DelayedWrapper delayedWrapper = this.delayedStore.queryDelayed(group , code);
        if(delayedWrapper == null){
            throw new SchedulerException("delayed not exist");
        }
        this.executeJob(delayedWrapper);
    }

    /**
     * 触发指定组的延时任务
     * @param group
     * @throws SchedulerException
     */
    public void triggerJob(String group)throws SchedulerException{
        List<DelayedWrapper> delayedWrappers = this.delayedStore.queryDelayed(group);
        if(CollectionUtils.isEmpty(delayedWrappers)){
            return;
        }
        for (DelayedWrapper wrapper : delayedWrappers){
            this.executeJob(wrapper);
        }
    }

    /**
     * 删除指定延时任务
     * @param group
     * @param code
     */
    public void deleteJob(String group, String code){
        this.delayedStore.deleteJob(group,code);
        this.notifyMainThread();
    }

    /**
     * 删除指定组的延时任务
     * @param group
     */
    public void deleteJob(String group){
        this.delayedStore.deleteJob(group);
        this.notifyMainThread();
    }

    /**
     * 暂停指定延时任务
     * @param group
     * @param code
     */
    public void pauseJob(String group, String code) throws SchedulerException, DelayedException{
        this.delayedStore.pauseJob(group, code);
        this.notifyMainThread();
    }

    /**
     * 暂停指定组的延时任务
     * @param group
     */
    public void pauseJob(String group) throws SchedulerException, DelayedException{
        this.delayedStore.pauseJob(group);
        this.notifyMainThread();
    }

    /**
     * 恢复指定延时任务
     * @param group
     * @param code
     */
    public void resumeJob(String group, String code){
        this.delayedStore.resumeJob(group, code);
        this.notifyMainThread();
    }

    /**
     * 恢复指定组的延时任务
     * @param group
     */
    public void resumeJob(String group){
        this.delayedStore.resumeJob(group);
        this.notifyMainThread();
    }

    private void executeJob(DelayedWrapper delayedWrapper){
        Job job = this.instanceJob(delayedWrapper.getJobClass());
        this.executor.execute(() -> {
            JobExecuteContext context = JobExecuteContext.create(delayedWrapper , this.scheduler);
            this.pluginBeforeExecuteJob(context);
            job.execute(context);
            this.pluginAfterExecuteJob(context);
        });
    }

    /**
     * 执行延时任务之前调用插件
     * @param context
     */
    private void pluginBeforeExecuteJob(JobExecuteContext context){
        if(CollectionUtils.isNotEmpty(this.plugins)){
            for (Plugin plugin : this.plugins){
                if(plugin.support(context.getDelayed().getGroup(),context.getDelayed().getCode())){
                    plugin.beforeExecute(context);
                }
            }
        }
    }

    /**
     * 执行延时任务之后调用插件
     * @param context
     */
    private void pluginAfterExecuteJob(JobExecuteContext context){
        if(CollectionUtils.isNotEmpty(this.plugins)){
            for (Plugin plugin : this.plugins){
                if(plugin.support(context.getDelayed().getGroup(),context.getDelayed().getCode())){
                    plugin.afterExecute(context);
                }
            }
        }
    }

    private Job instanceJob(Class<? extends Job> clx){
        try{
            return clx.newInstance();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通知主线程苏醒
     */
    public void notifyMainThread(){
        this.interrupt();
    }

    /**
     * 停止延时任务
     * @param isHalt
     */
    public void halt(Boolean isHalt){
        this.halted.set(isHalt);
        if(this.halted.get()){
            this.noticeListener(ActionEnum.HALT);
        }else {
            this.noticeListener(ActionEnum.START);
        }
    }

    /**
     * 暂停或者恢复延时任务
     * @param isPause
     */
    public void pause(Boolean isPause){
        this.paused.set(isPause);
        this.notifyMainThread();
        if(this.paused.get()){
            this.noticeListener(ActionEnum.PAUSE);
        }else {
            this.noticeListener(ActionEnum.RESUME);
        }
    }

    @Override
    public void run(){
        while (!this.halted.get()){
            //没有停止但是已经暂停了，则让主线程休眠
            while (!this.halted.get() && this.paused.get()){
                try{
                    synchronized (this.sigLock){
                        this.sigLock.wait(10000L);
                    }
                }catch (InterruptedException e){}
            }
            //如果调度已经停止了，则推出循环结束线程
            if(this.halted.get()){
                break;
            }
            try{
                //获取最早触发的前几位延时任务
                List<DelayedWrapper> delayeds = this.delayedStore.queryDelayedEarliestTrigger(size);
                if(CollectionUtils.isEmpty(delayeds)){
                    try{
                        synchronized (this.sigLock){
                            sigLock.wait(10000);
                        }
                        continue;
                    }catch (InterruptedException e){
                        delayeds = this.delayedStore.queryDelayedEarliestTrigger(size);
                    }
                }
                Integer flag = 0;
                for (DelayedWrapper delayed : delayeds){
                    long currentMill = System.currentTimeMillis();
                    long remainingTime = delayed.getDate().getTime() - currentMill;
                    //如果延时任务的触发时间已经过了，并且过期
                    if(delayed.getDate().getTime() < currentMill && remainingTime < 0){
                        if(remainingTime * -1 > threshold){
                            //过期时间大于阀值，不触发
                            continue;
                        } else {
                            //过期时间内小于阀值，立即触发
                            remainingTime = 5;
                        }
                    }
                    try{
                        //休眠等待触发时刻
                        System.out.println("休眠等待，当前时间戳：" + System.currentTimeMillis());
                        System.out.println("需要休眠时长：" + remainingTime);
                        synchronized (this.sigLock){
                            this.sigLock.wait(remainingTime);
                        }
                        System.out.println("休眠结束，当前时间戳：" + System.currentTimeMillis());
                        //准备执行调度任务
                        this.executeJob(delayed);
                        //消费延时任务，更改已经触发的延时任务状态
                        this.delayedStore.consumeDelayed(delayed);
                        flag ++;
                    } catch (InterruptedException e){
                        //休眠被中断，将还没有触发的延时任务重新放回去，注意只有延时任务的状态还是 processing 才更新状态为 wait
                        this.delayedStore.resetDelayed(delayeds.subList(flag,delayeds.size()));
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                LOGGER.error("scheduler an error occurred " + e.getMessage());
            }
        }
    }

    public List<Listener> getListeners(){
        return listeners;
    }

    public void addListeners(List<Listener> listeners){
        if(CollectionUtils.isEmpty(listeners)){
            return;
        }
        if(this.listeners == null){
            this.listeners = new ArrayList<>();
        }
        this.listeners.addAll(listeners);
    }

    public List<Plugin> getPlugins(){
        return plugins;
    }

    public void addPlugins(List<Plugin> plugins){
        if(CollectionUtils.isEmpty(plugins)){
            return;
        }
        if(this.plugins == null){
            this.plugins = new ArrayList<>();
        }
        this.plugins.addAll(plugins);
    }
}
