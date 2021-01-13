package com.bds.easy.delayed.core;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 16:59
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
public class JobExecuteContext{

    private Delayed delayed;
    private Scheduler scheduler;

    public JobExecuteContext(Delayed delayed,Scheduler scheduler){
        this.delayed = delayed;
        this.scheduler = scheduler;
    }

    public static JobExecuteContext create(Delayed delayed, Scheduler scheduler){
        return new JobExecuteContext(delayed,scheduler);
    }

    public Delayed getDelayed(){
        return delayed;
    }

    public void setDelayed(Delayed delayed){
        this.delayed = delayed;
    }

    public Scheduler getScheduler(){
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
    }
}
