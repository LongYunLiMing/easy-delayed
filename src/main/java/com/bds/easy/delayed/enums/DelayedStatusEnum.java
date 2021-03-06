package com.bds.easy.delayed.enums;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 14:41
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public enum DelayedStatusEnum{
    WAIT("WAIT"),
    PROCESSING("PROCESSING"),
    TRIGGERED("TRIGGERED"),
    PAUSE("PAUSE");
    private String status;

    DelayedStatusEnum(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
