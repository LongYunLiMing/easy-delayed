package com.bds.easy.delayed.store;

/**
 * description: 延时任务已存在异常
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 14:06
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public class DelayedException extends Exception{
    public DelayedException(){
    }

    public DelayedException(String message){
        super(message);
    }

    public DelayedException(String message , Throwable cause){
        super(message , cause);
    }

    public DelayedException(Throwable cause){
        super(cause);
    }

    public DelayedException(String message , Throwable cause , boolean enableSuppression , boolean writableStackTrace){
        super(message , cause , enableSuppression , writableStackTrace);
    }
}
