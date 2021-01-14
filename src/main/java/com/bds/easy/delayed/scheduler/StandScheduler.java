package com.bds.easy.delayed.scheduler;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.DelayedStore;
import com.bds.easy.delayed.core.Listener;
import com.bds.easy.delayed.core.Plugin;
import com.bds.easy.delayed.core.Scheduler;
import com.bds.easy.delayed.core.SchedulerException;
import com.bds.easy.delayed.core.SchedulerThread;
import com.bds.easy.delayed.store.DelayedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 调度器实现类
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/06 10:01
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/06
 */
public class StandScheduler implements Scheduler{
    private final Logger LOGGER = LoggerFactory.getLogger(StandScheduler.class);

    private SchedulerThread schedulerThread;

    public StandScheduler(DelayedStore delayedStore){
        this.schedulerThread = new SchedulerThread(new ThreadPoolExecutor(1 , 1 , 0L , TimeUnit.MILLISECONDS , new LinkedBlockingQueue<>()),delayedStore);
        this.schedulerThread.setScheduler(this);
    }

    public void setSchedulerThread(SchedulerThread schedulerThread){
        this.schedulerThread = schedulerThread;
    }

    @Override
    public void start() throws SchedulerException{
        if(schedulerThread.isHalted()){
            this.schedulerThread.halt(false);
            this.schedulerThread.start();
        }
        System.out.println("start delayed .................");
    }

    @Override
    public Boolean isStart() throws SchedulerException{
        return !this.schedulerThread.isHalted();
    }

    @Override
    public void shutdown() throws SchedulerException{
        this.schedulerThread.halt(true);
        this.schedulerThread.notifyMainThread();
    }

    @Override
    public void shutdown(Long millisecond) throws SchedulerException{
        try{
            Thread.sleep(millisecond);
        } catch (InterruptedException e){
            LOGGER.info("wait shutdown fail ......");
        }
        this.schedulerThread.halt(true);
    }

    @Override
    public boolean isShutdown() throws SchedulerException{
        return this.schedulerThread.isHalted();
    }

    @Override
    public void scheduleJob(Delayed delayed) throws SchedulerException{
        Delayed wrapper = schedulerThread.getDelayedStore().queryDelayed(delayed.getGroup() , delayed.getCode());
        if(wrapper != null){
            throw new SchedulerException("group and code not be repeat —— [" + delayed.getGroup() + "],[" + delayed.getCode() + "]");
        }
        try{
            schedulerThread.getDelayedStore().insertDelayed(delayed);
        } catch (DelayedException e){
            e.printStackTrace();
        }
        this.schedulerThread.notifyMainThread();
    }

    @Override
    public void triggerJob(String group , String code) throws SchedulerException{
        this.schedulerThread.triggerJob(group, code);
    }

    @Override
    public void triggerJob(String group) throws SchedulerException{
        this.schedulerThread.triggerJob(group);
    }

    @Override
    public void deleteJob(String group , String code) throws SchedulerException{
        this.schedulerThread.deleteJob(group, code);
    }

    @Override
    public void deleteJob(String group) throws SchedulerException{
        this.schedulerThread.deleteJob(group);
    }

    @Override
    public void pauseJob(String group , String code) throws SchedulerException, DelayedException{
        this.schedulerThread.pauseJob(group, code);
    }

    @Override
    public void pauseJob(String group) throws SchedulerException, DelayedException{
        this.schedulerThread.pauseJob(group);
    }

    @Override
    public void resumeJob(String group , String code) throws SchedulerException{
        this.schedulerThread.resumeJob(group, code);
    }

    @Override
    public void resumeJob(String group) throws SchedulerException{
        this.schedulerThread.resumeJob(group);
    }

    @Override
    public void addListeners(List<Listener> listeners){
        this.schedulerThread.addListeners(listeners);
    }

    @Override
    public void addPlugins(List<Plugin> plugins){
        this.schedulerThread.addPlugins(plugins);
    }

    @Override
    public void pause(){
        this.schedulerThread.pause(true);
    }

    @Override
    public void resume(){
        this.schedulerThread.pause(false);
    }
}
