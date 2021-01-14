package com.bds.easy.delayed.job;

import com.bds.easy.delayed.core.JobExecuteContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/13 14:56
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/13
 */
public class DemoContinueJob extends ContinueJob{
    @Override
    public void handle(JobExecuteContext context,Integer count){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("第 "+ count +" 次触发延时任务 —— 当前时间："+sdf.format(new Date()));
    }

    @Override
    public Long spacingTime(){
        return Long.valueOf(1000 * 60);
    }
}
