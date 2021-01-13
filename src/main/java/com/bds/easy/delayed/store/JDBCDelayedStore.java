package com.bds.easy.delayed.store;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.DelayedStatusEnum;
import com.bds.easy.delayed.core.DelayedStore;
import com.bds.easy.delayed.core.DelayedWrapper;
import com.bds.easy.delayed.mapper.DelayedMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 17:27
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
public class JDBCDelayedStore implements DelayedStore{

    @Autowired
    private DelayedMapper mapper;

    @Override
    public void insertDelayed(Delayed delayed) throws DelayedException{
        DelayedWrapper wrapper = DelayedWrapper.wrapper(delayed);
        wrapper.setStatus("wait");
         mapper.insert(wrapper);
    }

    @Override
    public List<DelayedWrapper> queryDelayedEarliestTrigger(Integer size) throws DelayedException{
        List<DelayedWrapper> delayedWrappers = mapper.queryDelayedEarliestTrigger(size);
        if(CollectionUtils.isNotEmpty(delayedWrappers)){
            Set<Long> ids = delayedWrappers.stream().map(DelayedWrapper :: getId).collect(Collectors.toSet());
            mapper.updateStatusByIds(ids,"processing");
        }
        return delayedWrappers;
    }

    @Override
    public void resetDelayed(List<DelayedWrapper> wrappers) throws DelayedException{
        if(CollectionUtils.isEmpty(wrappers)){
            return;
        }
        mapper.resetDelayed(wrappers.stream().map(DelayedWrapper::getId).collect(Collectors.toSet()));
    }

    @Override
    public DelayedWrapper queryDelayed(String group , String code){
        return mapper.selectOne(new DelayedWrapper(group, code));
    }

    @Override
    public List<DelayedWrapper> queryDelayed(String group){
        return mapper.select(new DelayedWrapper(group));
    }

    @Override
    public void deleteJob(String group , String code){
        mapper.delete(new DelayedWrapper(group, code));
    }

    @Override
    public void deleteJob(String group){
        mapper.delete(new DelayedWrapper(group));
    }

    @Override
    public void pauseJob(String group , String code) throws DelayedException{
        mapper.updateStatus(group, code, "pause");
    }

    @Override
    public void pauseJob(String group) throws DelayedException{
        mapper.updateStatusByGroup(group, "pause");
    }

    @Override
    public void resumeJob(String group , String code){
        mapper.updateStatus(group, code, "wait");
    }

    @Override
    public void resumeJob(String group){
        mapper.updateStatusByGroup(group, "wait");
    }

    @Override
    public void consumeDelayed(DelayedWrapper delayed){
        mapper.updateStatusByIds(Collections.singleton(delayed.getId()) , DelayedStatusEnum.TRIGGERED.getStatus());
    }
}
