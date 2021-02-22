package com.bds.easy.delayed.store;

import com.bds.easy.delayed.core.Delayed;
import com.bds.easy.delayed.core.DelayedStore;
import com.bds.easy.delayed.enums.DelayedStatusEnum;
import org.apache.commons.collections.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    public static final String FIELD_ID = "ID";
    public static final String FIELD_STATUS = "STATUS";
    public static final String FIELD_GROUP = "GROUP";
    public static final String FIELD_CODE = "CODE";
    public static final String FIELD_JOB_CLASS = "JOB_CLASS";
    public static final String FIELD_DATE = "DATE";
    public static final String FIELD_NAME = "NAME";
    public static final String FIELD_DESCRIPTION = "DESCRIPTION";
    public static final String FIELD_PARAM = "PARAM";
    public static final String INSERT_SQL = "INSERT INTO `DELAYED_JOB`(`ID`, `STATUS`, `GROUP`, `CODE`, `JOB_CLASS`, `DATE`, `NAME`, `DESCRIPTION`, `PARAM`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    public static final String QUERY_DELAYED_EARLIEST_TRIGGER_SQL = "SELECT ID,STATUS,`GROUP`,CODE,DATE,JOB_CLASS,NAME,DESCRIPTION,PARAM FROM DELAYED_JOB WHERE STATUS IN ('WAIT','PROCESSING') AND DATE > CURRENT_TIMESTAMP ORDER BY DATE ASC LIMIT 0, ?";
    public static final String UPDATE_STATUS_BY_IDS_SQL = "UPDATE DELAYED_JOB SET STATUS = ? WHERE ID IN (IDS)";
    public static final String RESET_DELAYED_SQL = "UPDATE DELAYED_JOB SET STATUS = 'WAIT' STATUS = 'PROCESSING' AND ID IN (IDS)";
    public static final String SELECT_BY_GROUP_AND_CODE_SQL = "SELECT ID,STATUS,`GROUP`,CODE,DATE,JOB_CLASS,NAME,DESCRIPTION,PARAM FROM DELAYED_JOB WHERE `GROUP` = ? AND CODE = ?";
    public static final String SELECT_BY_GROUP_SQL = "SELECT ID,STATUS,`GROUP`,CODE,DATE,JOB_CLASS,NAME,DESCRIPTION,PARAM FROM DELAYED_JOB WHERE `GROUP` = ?";
    public static final String DELETE_BY_GROUP_AND_CODE_SQL = "DELETE FROM `DELAYED_JOB` WHERE `GROUP` = ? AND CODE = ?";
    public static final String DELETE_BY_GROUP_SQL = "DELETE FROM `DELAYED_JOB` WHERE `GROUP` = ?";
    public static final String DELETE_BY_ID_SQL = "DELETE FROM `DELAYED_JOB` WHERE ID = ?";
    public static final String UPDATE_STATUS_BY_GROUP_CODE_SQL = "UPDATE DELAYED_JOB SET STATUS = ? WHERE `GROUP` = ? AND CODE = ?";
    public static final String UPDATE_STATUS_BY_GROUP_SQL = "UPDATE DELAYED_JOB SET STATUS = ? WHERE `GROUP` = ?";

    private Connection connection;

    public JDBCDelayedStore(DataSource dataSource){
        try{
            connection = dataSource.getConnection();
        }catch (SQLException e){
            throw new DelayedException(e);
        } catch (DelayedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertDelayed(Delayed delayed) throws DelayedException{
        delayed.setStatus(DelayedStatusEnum.WAIT.getStatus());
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(INSERT_SQL);
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2,delayed.getStatus());
            ps.setString(3,delayed.getGroup());
            ps.setString(4,delayed.getCode());
            ps.setString(5,delayed.getJobClass().getName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(delayed.getDate() != null){
                ps.setTimestamp(6,new Timestamp(delayed.getDate().getTime()));
            }
            ps.setString(7,delayed.getName());
            ps.setString(8,delayed.getDescription());
            ps.setString(9,delayed.getParam());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        } finally{
            closeStatement(ps);
        }
    }

    @Override
    public List<Delayed> queryDelayedEarliestTrigger(Integer size) throws DelayedException{
        PreparedStatement ps = null;
        PreparedStatement updatePs = null;
        List<Delayed> result = new ArrayList<>();
        try{
            ps = this.connection.prepareStatement(QUERY_DELAYED_EARLIEST_TRIGGER_SQL);
            ps.setInt(1,size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                result.add(Delayed.getInstance(rs));
            }
            if(CollectionUtils.isNotEmpty(result)){
                Set<String> ids = result.stream().map(Delayed :: getId).collect(Collectors.toSet());
                String sql = this.getUpdateStatusSQL(ids.size());
                updatePs = this.connection.prepareStatement(sql);
                updatePs.setString(1,DelayedStatusEnum.PROCESSING.getStatus());
                Integer index = 2;
                for (String id : ids){
                    updatePs.setString(index,id);
                    index ++;
                }
                updatePs.executeUpdate();
            }
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
            closeStatement(updatePs);
        }
        return result;
    }

    private String getUpdateStatusSQL(Integer size){
        StringBuilder ids = new StringBuilder();
        for (Integer i = 0 ; i < size ; i++){
            if(i != 0){
                ids.append(",?");
            } else {
                ids.append("?");
            }
        }
        return UPDATE_STATUS_BY_IDS_SQL.replace("IDS" , ids);
    }

    @Override
    public void resetDelayed(List<Delayed> delayeds) throws DelayedException{
        if(CollectionUtils.isEmpty(delayeds)){
            return;
        }
        Set<String> idSet= delayeds.stream().map(Delayed :: getId).collect(Collectors.toSet());
        StringBuilder ids = new StringBuilder();
        for (Integer i = 0 ; i < idSet.size() ; i++){
            if(i != 0){
                ids.append(",?");
            } else {
                ids.append("?");
            }
        }
        String sql = RESET_DELAYED_SQL.replace("IDS" , ids);
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(sql);
            Integer index = 1;
            for (String id : idSet){
                ps.setString(index,id);
                index ++;
            }
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    @Override
    public Delayed queryDelayed(String group , String code) throws DelayedException{
        PreparedStatement ps = null;
        Delayed result = null;
        try{
            ps = this.connection.prepareStatement(SELECT_BY_GROUP_AND_CODE_SQL);
            ps.setString(1,group);
            ps.setString(2,code);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = Delayed.getInstance(rs);
            }
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
        return result;
    }

    @Override
    public List<Delayed> queryDelayed(String group) throws DelayedException{
        PreparedStatement ps = null;
        List<Delayed> result = null;
        try{
            ps = this.connection.prepareStatement(SELECT_BY_GROUP_SQL);
            ps.setString(1,group);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                result.add(Delayed.getInstance(rs));
            }
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
        return result;
    }

    @Override
    public void deleteJob(String group , String code) throws DelayedException{
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(DELETE_BY_GROUP_AND_CODE_SQL);
            ps.setString(1,group);
            ps.setString(2,code);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    @Override
    public void deleteJob(String group) throws DelayedException{
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(DELETE_BY_GROUP_SQL);
            ps.setString(1,group);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    @Override
    public void pauseJob(String group , String code) throws DelayedException{
        this.updateStatusByGroupCode(DelayedStatusEnum.PAUSE.getStatus(),group,code);
    }

    private void updateStatusByGroupCode(String status, String group, String code) throws DelayedException{
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(UPDATE_STATUS_BY_GROUP_CODE_SQL);
            ps.setString(1,status);
            ps.setString(2,group);
            ps.setString(3,code);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    @Override
    public void pauseJob(String group) throws DelayedException{
        this.updateStatusByGroup(DelayedStatusEnum.PAUSE.getStatus(),group);
    }

    private void updateStatusByGroup(String status, String group) throws DelayedException{
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(UPDATE_STATUS_BY_GROUP_SQL);
            ps.setString(1,status);
            ps.setString(2,group);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    @Override
    public void resumeJob(String group , String code) throws DelayedException{
        this.updateStatusByGroupCode(DelayedStatusEnum.WAIT.getStatus(),group, code);
    }

    @Override
    public void resumeJob(String group) throws DelayedException{
        this.updateStatusByGroup(DelayedStatusEnum.WAIT.getStatus(),group );
    }

    @Override
    public void consumeDelayed(Delayed delayed) throws DelayedException{
        PreparedStatement ps = null;
        try{
            ps = this.connection.prepareStatement(DELETE_BY_ID_SQL);
            ps.setString(1,delayed.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new DelayedException(e);
        }finally{
            closeStatement(ps);
        }
    }

    protected static void closeStatement(Statement statement) {
        if (null != statement) {
            try {
                statement.close();
            } catch (SQLException ignore) {
            }
        }
    }
}
