package com.bds.easy.delayed.core;

/**
 * description: 插件
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 13:53
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public interface Plugin {

    /**
     * 执行之前被调用
     * @param context
     */
    void beforeExecute(JobExecuteContext context);

    /**
     * 执行之后被调用
     * @param context
     */
    void afterExecute(JobExecuteContext context);

    /**
     * 是否支持插件
     * @param group
     * @param code
     */
    default Boolean support(String group, String code){
        return true;
    }

    /**
     * 执行顺序
     * @return
     */
    default Integer order(){
        return 0;
    }
}
