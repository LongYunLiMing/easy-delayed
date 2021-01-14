package com.bds.easy.delayed.mapper;

import com.bds.easy.delayed.baseMapper.MyMapper;
import com.bds.easy.delayed.core.Delayed;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 17:24
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
public interface DelayedMapper extends MyMapper<Delayed>{

    /**
     * 查询最早触发的前几位
     * @param size
     * @return
     */
    List<Delayed> queryDelayedEarliestTrigger(@Param("size") Integer size);

    /**
     * 将当前延时任务从 processing 重新变成 wait
     * @param ids
     */
    void resetDelayed(@Param("ids") Set<Long> ids);

    /**
     * 更改指定延时任务的状态
     * @param group
     * @param code
     * @param status
     */
    void updateStatus(@Param("group") String group, @Param("code") String code , @Param("status") String status);

    /**
     * 改变指定组的状态
     * @param group
     * @param status
     */
    void updateStatusByGroup(@Param("group")String group, @Param("status") String status);

    /**
     * 批量更新延时任务的状态
     * @param ids
     * @param processing
     */
    void updateStatusByIds(@Param("ids") Set<Long> ids , @Param("status") String processing);
}
