package com.bds.easy.delayed.core;

import com.bds.easy.delayed.store.DelayedException;

import java.util.List;

/**
 * description: 调度器
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 09:59
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public interface Scheduler{

    /**
     * 开启调度
     */
    void start() throws SchedulerException;

    /**
     * 是否开启调度
     */
    Boolean isStart() throws SchedulerException;

    /**
     * 关闭调度器
     */
    void shutdown() throws SchedulerException;

    /**
     * 指定时长后关闭调度器
     */
    void shutdown(Long millisecond) throws SchedulerException;

    /**
     * 是否关闭调度器
     */
    boolean isShutdown() throws SchedulerException;

    /**
     * 调度延时任务
     */
    void scheduleJob(Delayed delayed) throws SchedulerException;

    /**
     * 触发指定延时任务
     * @param group 组
     * @param code  code
     */
    void triggerJob(String group, String code) throws SchedulerException;

    /**
     * 触发指定组的延时任务
     * @param group
     */
    void triggerJob(String group)throws SchedulerException;

    /**
     * 删除指定延时任务
     * @param group 组
     * @param code  code
     */
    void deleteJob(String group,String code) throws SchedulerException;

    /**
     * 删除指定组的延时任务
     * @param group
     */
    void deleteJob(String group)throws SchedulerException;

    /**
     * 暂停指定延时任务
     * @param group
     * @param code
     */
    void pauseJob(String group, String code) throws SchedulerException, DelayedException;

    /**
     * 暂停指定组的延时任务
     * @param group
     */
    void pauseJob(String group) throws SchedulerException, DelayedException;

    /**
     * 恢复指定延时任务
     * @param group
     * @param code
     */
    void resumeJob(String group, String code)throws SchedulerException;

    /**
     * 恢复指定组的延时任务
     * @param group
     */
    void resumeJob(String group)throws SchedulerException;

    /**
     * 添加监听器
     * @param listeners
     */
    void addListeners(List<Listener> listeners);

    /**
     * 添加插件
     * @param plugins
     */
    void addPlugins(List<Plugin> plugins);

    /**
     * 暂停
     */
    void pause();

    /**
     * 恢复
     */
    void resume();
}
