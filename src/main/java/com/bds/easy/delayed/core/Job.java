package com.bds.easy.delayed.core;

/**
 * description: Job 接口
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 16:06
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
public interface Job{
    void execute(JobExecuteContext context);
}
