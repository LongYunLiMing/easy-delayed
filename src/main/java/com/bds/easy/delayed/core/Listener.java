package com.bds.easy.delayed.core;

/**
 * description: 监听器
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/05 14:25
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/05
 */
public interface Listener{

    /**
     * 开始时回调
     */
    void startAction();

    /**
     * 停止时回调
     */
    void haltAction();

    /**
     * 暂停时回调
     */
    void pauseAction();

    /**
     * 恢复暂停时回调
     */
    void resumeAction();
    
}
