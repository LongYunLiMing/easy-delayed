package com.bds.easy.delayed.core;

import com.bds.easy.delayed.store.DelayedException;

import java.util.List;

/**
 * description: 延时信息注册器
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 09:48
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public interface DelayedStore{

    /**
     * 创建延时任务
     * @param delayed
     * @return
     */
    void insertDelayed(Delayed delayed) throws DelayedException;

    /**
     * 查询最早触发的前几条延时任务
     * @param size
     * @return
     */
    List<DelayedWrapper> queryDelayedEarliestTrigger(Integer size) throws DelayedException;

    /**
     * 重置延时任务
     * @param wrappers
     * @throws DelayedException
     */
    void resetDelayed(List<DelayedWrapper> wrappers) throws DelayedException;

    /**
     * 查询指定的延时任务
     * @param group
     * @param code
     * @return
     */
    DelayedWrapper queryDelayed(String group, String code);

    /**
     * 查询指定组的延时任务
     * @param group
     * @return
     */
    List<DelayedWrapper> queryDelayed(String group);

    /**
     * 删除指定的延时任务
     * @param group
     * @param code
     */
    void deleteJob(String group , String code);

    /**
     * 删除指定组的延时任务
     * @param group
     */
    void deleteJob(String group);

    /**
     * 暂停指定延时任务
     * @param group
     * @param code
     */
    void pauseJob(String group, String code) throws DelayedException;

    /**
     * 暂停指定组的延时任务
     * @param group
     */
    void pauseJob(String group) throws DelayedException;

    /**
     * 恢复指定延时任务
     * @param group
     * @param code
     */
    void resumeJob(String group, String code);

    /**
     * 恢复指定组的延时任务
     * @param group
     */
    void resumeJob(String group);

    /**
     * 消费延时任务
     * @param delayed
     */
    void consumeDelayed(DelayedWrapper delayed);
}