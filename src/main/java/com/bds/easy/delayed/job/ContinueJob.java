package com.bds.easy.delayed.job;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.Job;
import com.bds.easy.delayed.core.JobExecuteContext;
import com.bds.easy.delayed.core.SchedulerException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 连续的延时任务，隔一段时间触发一次延时任务
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/13 14:38
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/13
 */
public abstract class ContinueJob implements Job{

    @Override
    public void execute(JobExecuteContext context){
        Delayed delayed = context.getDelayed();
        Map<String, String> param = delayed.getParamMap();

        if(param == null || !param.containsKey("count")){
            param = new HashMap<>();
            param.put("count","1");
            context.getDelayed().setParamMap(param);
        }
        handle(context,Integer.valueOf(param.get("count")));
        String count = param.get("count");
        param.put("count",String.valueOf(Long.valueOf(count)+1));
        delayed.setCode(delayed.getCode() + ":" + System.currentTimeMillis());
        delayed.setDate(new Date(System.currentTimeMillis() + spacingTime()));
        delayed.setId(null);
        try{
            context.getScheduler().scheduleJob(delayed);
        } catch (SchedulerException e){
            e.printStackTrace();
        }
    }

    public abstract void handle(JobExecuteContext context,Integer count);

    public abstract Long spacingTime();
}
