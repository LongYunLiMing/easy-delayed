package com.bds.easy.delayed.listener;

import com.bds.easy.delayed.core.Listener;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/11 22:53
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/11
 */
@Component
public class LogListenerr implements Listener{

    @Override
    public void startAction(){
        this.log("开启延时服务");
    }

    @Override
    public void haltAction(){
        this.log("停止延时服务");
    }

    @Override
    public void pauseAction(){
        this.log("暂停延时服务");
    }

    @Override
    public void resumeAction(){
        this.log("恢复延时服务");
    }

    private void log(String action){
        System.out.println("================"+action+"================");
    }
}
