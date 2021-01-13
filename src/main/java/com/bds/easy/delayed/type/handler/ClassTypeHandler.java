package com.bds.easy.delayed.type.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 18:07
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
@MappedTypes(Class.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ClassTypeHandler extends BaseTypeHandler<Class>{

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement , int i , Class aClass , JdbcType jdbcType) throws SQLException{
        preparedStatement.setString(i,aClass.getName());
    }

    @Override
    public Class getNullableResult(ResultSet resultSet , String s) throws SQLException{
        try{
            return Class.forName(resultSet.getString(s));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class getNullableResult(ResultSet resultSet , int i) throws SQLException{
        try{
            return Class.forName(resultSet.getString(i));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class getNullableResult(CallableStatement callableStatement , int i) throws SQLException{
        try{
            return Class.forName(callableStatement.getString(i));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}