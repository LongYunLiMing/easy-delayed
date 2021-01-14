package com.bds.easy.delayed.store;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.DelayedStatusEnum;
import com.bds.easy.delayed.core.DelayedStore;
import com.bds.easy.delayed.core.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * description: 内存管理延时任务
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/30 14:04
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/30
 */
public class MemoryDelayedStore implements DelayedStore{

    private TreeSet<Delayed> waitDelayed = new TreeSet<>((a, b) -> (int) (a.getDate().getTime() - b.getDate().getTime()));
    private Map<String,Map<String,Delayed>> notWaitDelayed = new HashMap<>();

    @Override
    public void insertDelayed(Delayed delayed) throws DelayedException{
        if(delayed == null){
            throw new DelayedException("delayed not be null");
        }
        if(StringUtils.isBlank(delayed.getGroup())){
            throw new DelayedException("group not be blank");
        }
        if(StringUtils.isBlank(delayed.getCode())){
            throw new DelayedException("code not be blank");
        }
        if(delayed.getJobClass() == null){
            throw new DelayedException("job class not be null");
        }
        if(delayed.getDate() == null){
            throw new DelayedException("timestamp not be null");
        }
        if(delayed.getDate().before(new Date())){
            throw new DelayedException("job will never be triggered ——（" + delayed.getDate() + "）");
        }
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),delayed.getGroup()) && StringUtils.equals(wrapper.getCode(),delayed.getCode())){
                throw new DelayedException("group and code not be repeat —— [" + delayed.getGroup() + "],[" + delayed.getCode() + "]");
            }
        }
        if(this.notWaitDelayed.containsKey(delayed.getGroup()) && this.notWaitDelayed.get(delayed.getGroup()).containsKey(delayed.getCode())){
            throw new DelayedException("group and code not be repeat —— [" + delayed.getGroup() + "],[" + delayed.getCode() + "]");
        }
        delayed.setStatus(DelayedStatusEnum.WAIT.getStatus());
        waitDelayed.add(delayed);
    }

    @Override
    public List<Delayed> queryDelayedEarliestTrigger(Integer size) throws DelayedException{
        List<Delayed> result = new ArrayList<>();
        for (Integer i = 0 ; i < size ; i++){
            Delayed wrapper = waitDelayed.pollFirst();
            if(wrapper == null){
                break;
            }
            wrapper.setStatus(DelayedStatusEnum.PROCESSING.getStatus());
            result.add(wrapper);
            this.addNotWaitMap(wrapper);
        }
        return result;
    }

    @Override
    public void resetDelayed(List<Delayed> wrappers) throws DelayedException{
        for (Delayed wrapper : wrappers){
            if(this.notWaitDelayed.containsKey(wrapper.getGroup()) && this.notWaitDelayed.get(wrapper.getGroup()).containsKey(wrapper.getCode()) && StringUtils.equals(this.notWaitDelayed.get(wrapper.getGroup()).get(wrapper.getCode()).getStatus(),DelayedStatusEnum.PROCESSING.getStatus())){
                this.notWaitDelayed.get(wrapper.getGroup()).remove(wrapper.getCode());
                wrapper.setStatus(DelayedStatusEnum.WAIT.getStatus());
                this.waitDelayed.add(wrapper);
            }
        }
    }

    private void addNotWaitMap(Delayed target) throws DelayedException{
        if(this.notWaitDelayed.containsKey(target.getGroup())){
            if(this.notWaitDelayed.get(target.getGroup()).containsKey(target.getCode())){
                throw new DelayedException("group and code not be repeat —— [" + target.getGroup() + "],[" + target.getCode() + "]");
            }else {
                this.notWaitDelayed.get(target.getGroup()).put(target.getCode(),target);
            }
        }else {
            Map<String,Delayed> map = new HashMap<>();
            map.put(target.getCode(),target);
            this.notWaitDelayed.put(target.getGroup(),map);
        }
    }

    @Override
    public Delayed queryDelayed(String group , String code){
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),group) && StringUtils.equals(wrapper.getCode(),code)){
                return wrapper;
            }
        }
        if(this.notWaitDelayed.containsKey(group) && this.notWaitDelayed.get(group).containsKey(code)){
            return this.notWaitDelayed.get(group).get(code);
        }
        return null;
    }

    @Override
    public List<Delayed> queryDelayed(String group){
        List<Delayed> result = queryJobFromWait(group);
        if(this.notWaitDelayed.containsKey(group) ){
            result.addAll(this.notWaitDelayed.get(group).values());
        }
        return result;
    }

    @Override
    public void deleteJob(String group , String code){
        Delayed target = null;
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),group) && StringUtils.equals(wrapper.getCode(),code)){
                target = wrapper;
            }
        }
        if(target != null){
            this.waitDelayed.remove(target);
        }
        if(this.notWaitDelayed.containsKey(group) && this.notWaitDelayed.get(group).containsKey(code)){
            this.notWaitDelayed.get(group).remove(code);
        }
    }

    @Override
    public void deleteJob(String group){
        List<Delayed> target = queryJobFromWait(group);
        if(CollectionUtils.isNotEmpty(target)){
            for (Delayed wrapper : target){
                this.waitDelayed.remove(wrapper);
            }
        }
        if(this.notWaitDelayed.containsKey(group) ){
            this.notWaitDelayed.remove(group);
        }
    }

    private void look(String group, String code, Function<Delayed> function){
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),group) && StringUtils.equals(wrapper.getCode(),code)){
                function.deal(wrapper);
            }
        }
    }



    @Override
    public void pauseJob(String group , String code) throws DelayedException{
        AtomicReference<Delayed> target = new AtomicReference<>();
        this.look(group , code , wrapper -> target.set(wrapper));
        Delayed wrapper = target.get();
        if( wrapper != null){
            this.waitDelayed.remove(wrapper);
            wrapper.setStatus(DelayedStatusEnum.PAUSE.getStatus());
            this.addNotWaitMap(wrapper);
        }
        if(this.notWaitDelayed.containsKey(group) && this.notWaitDelayed.get(group).containsKey(code)){
            Delayed delayedWrapper = this.notWaitDelayed.get(group).get(code);
            delayedWrapper.setStatus(DelayedStatusEnum.PAUSE.getStatus());
            this.notWaitDelayed.get(group).remove(code);
            this.addNotWaitMap(delayedWrapper);
        }
    }

    @Override
    public void pauseJob(String group) throws DelayedException{
        List<Delayed> target = new ArrayList<>();
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),group)){
                target.add(wrapper);
            }
        }
        if(this.notWaitDelayed.containsKey(group)){
            target.addAll(this.notWaitDelayed.get(group).values());
            this.notWaitDelayed.remove(group);
        }
        for (Delayed wrapper : target){
            this.waitDelayed.remove(wrapper);
            wrapper.setStatus(DelayedStatusEnum.PAUSE.getStatus());
            this.addNotWaitMap(wrapper);
        }
    }

    @Override
    public void resumeJob(String group, String code){
        if(this.notWaitDelayed.containsKey(group) && this.notWaitDelayed.get(group).containsKey(code)){
            Delayed wrapper = this.notWaitDelayed.get(group).get(code);
            wrapper.setStatus(DelayedStatusEnum.WAIT.getStatus());
            this.waitDelayed.add(wrapper);
            this.notWaitDelayed.get(group).remove(code);
        }
    }

    @Override
    public void resumeJob(String group){
        if(this.notWaitDelayed.containsKey(group)){
            Map<String, Delayed> wrapperMap = this.notWaitDelayed.get(group);
            for (Delayed wrapper : wrapperMap.values()){
                wrapper.setStatus(DelayedStatusEnum.WAIT.getStatus());
                this.waitDelayed.add(wrapper);
            }
            this.notWaitDelayed.remove(group);
        }
    }

    private List<Delayed> queryJobFromWait(String group){
        List<Delayed> target = new ArrayList<>();
        for (Delayed wrapper : this.waitDelayed){
            if(StringUtils.equals(wrapper.getGroup(),group)){
                target.add(wrapper);
            }
        }
        return target;
    }

    @Override
    public void consumeDelayed(Delayed delayed){
        if(this.notWaitDelayed.containsKey(delayed.getGroup()) && this.notWaitDelayed.get(delayed.getGroup()).containsKey(delayed.getCode())){
            this.notWaitDelayed.get(delayed.getGroup()).get(delayed.getCode()).setStatus(DelayedStatusEnum.TRIGGERED.getStatus());
        }
    }
}
