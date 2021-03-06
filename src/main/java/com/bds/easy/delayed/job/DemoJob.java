package com.bds.easy.delayed.job;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.Job;
import com.bds.easy.delayed.core.JobExecuteContext;
import com.bds.easy.delayed.core.Scheduler;
import com.bds.easy.delayed.core.SchedulerException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/06 10:55
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/06
 */
public class DemoJob implements Job{
    @Override
    public void execute(JobExecuteContext context){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("触发延时任务 —— 当前时间："+sdf.format(new Date()));
    }
}
