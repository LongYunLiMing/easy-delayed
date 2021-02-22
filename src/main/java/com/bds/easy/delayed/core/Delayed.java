package com.bds.easy.delayed.core;

import com.alibaba.fastjson.JSON;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_CODE;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_DATE;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_DESCRIPTION;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_GROUP;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_ID;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_JOB_CLASS;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_NAME;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_PARAM;
import static com.bds.easy.delayed.store.JDBCDelayedStore.FIELD_STATUS;

/**
 * description: 延时队列信息
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 17:01
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
public class Delayed{

    public Delayed(){}

    public Delayed(String group){
        this.group = group;
    }

    public Delayed(String group , String code){
        this(group,code,null,null);
    }

    public Delayed(String group , String code, Date date, Class<? extends Job> jobClass){
        this.group = group;
        this.code = code;
        this.date = date;
        this.jobClass = jobClass;
    }

    private Long id;
    private String status;
    //组（必填）
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

    public void setJobClass(String jobClass){
        try{
            this.jobClass = (Class<? extends Job>) Class.forName(jobClass);
        } catch (Exception e){
            throw new RuntimeException("job class error");
        }
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

    public static Delayed getInstance(ResultSet rs) throws SQLException{
        Delayed delayed = new Delayed();
        delayed.setId(rs.getLong(FIELD_ID));
        delayed.setJobClass(rs.getString(FIELD_JOB_CLASS));
        delayed.setName(rs.getString(FIELD_NAME));
        delayed.setCode(rs.getString(FIELD_CODE));
        delayed.setGroup(rs.getString(FIELD_GROUP));
        delayed.setStatus(rs.getString(FIELD_STATUS));
        delayed.setDescription(rs.getString(FIELD_DESCRIPTION));
        delayed.setParam(rs.getString(FIELD_PARAM));
        java.sql.Date date = rs.getDate(FIELD_DATE);
        if(date != null){
            delayed.setDate(new java.util.Date(date.getTime()));
        }
        return delayed;
    }
}
