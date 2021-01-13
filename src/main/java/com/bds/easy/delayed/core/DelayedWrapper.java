package com.bds.easy.delayed.core;

import javax.persistence.Table;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 14:35
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
@Table(name = "delayed_job")
public class DelayedWrapper extends Delayed{
    public DelayedWrapper(){}

    public DelayedWrapper(String group){
        super(group);
    }

    public DelayedWrapper(String group, String code){
        super(group, code);
    }

    private String status;

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public static DelayedWrapper wrapper(Delayed delayed){
        DelayedWrapper delayedWrapper = new DelayedWrapper();
        delayedWrapper.setGroup(delayed.getGroup());
        delayedWrapper.setCode(delayed.getCode());
        delayedWrapper.setJobClass(delayed.getJobClass());
        delayedWrapper.setName(delayed.getName());
        delayedWrapper.setDate(delayed.getDate());
        return delayedWrapper;
    }

    public static DelayedWrapper wrapper(Delayed delayed,DelayedStatusEnum statusEnum){
        DelayedWrapper wrapper = wrapper(delayed);
        wrapper.setStatus(statusEnum.getStatus());
        return wrapper;
    }
}
