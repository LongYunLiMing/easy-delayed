package com.bds.easy.delayed.plugin;

import com.bds.easy.delayed.core.JobExecuteContext;
import com.bds.easy.delayed.core.Plugin;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/11 22:56
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/11
 */
@Component
public class LogPlugin implements Plugin{

    @Override
    public void afterExecute(JobExecuteContext context){
        this.log("执行结束了",context);
    }

    @Override
    public void beforeExecute(JobExecuteContext context){
        this.log("开始执行了",context);
    }

    @Override
    public Boolean support(String group , String code){
        return true;
    }

    @Override
    public Integer order(){
        return 0;
    }

    private void log(String action,JobExecuteContext context){
        System.out.println("================"+action+"【"+context.getDelayed().getGroup()+";"+context.getDelayed().getCode()+"】");
    }
}
