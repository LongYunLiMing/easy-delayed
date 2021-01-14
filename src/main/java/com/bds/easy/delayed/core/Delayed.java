package com.bds.easy.delayed.core;

import com.alibaba.fastjson.JSON;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

/**
 * description: 延时队列信息
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 17:01
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
@Table(name = "delayed_job")
public class Delayed{

    public Delayed(){}

    public Delayed(String group){
        this.group = group;
    }

    public Delayed(String group , String code){
        this.group = group;
        this.code = code;
    }

    private Long id;
    private String status;
    //组（必填）
    @Column(name = "`group`")
    private String group;
    //code（必填）
    private String code;
    //名称
    private String name;
    //触发的时间戳（必填）
    private Date date;
    //执行 Job(必填)
    private Class<? extends Job> jobClass;

    private String description;

    private String param;

    private Map<String,String> paramMap;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getGroup(){
        return group;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public Class<? extends Job> getJobClass(){
        return jobClass;
    }

    public void setJobClass(Class<? extends Job> jobClass){
        this.jobClass = jobClass;
    }

    public String getParam(){
        return param;
    }

    public void setParam(String param){
        this.param = param;
        this.paramMap = JSON.parseObject(param,Map.class);
    }

    public Map<String, String> getParamMap(){
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap){
        this.paramMap = paramMap;
        this.param = JSON.toJSONString(paramMap);
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
