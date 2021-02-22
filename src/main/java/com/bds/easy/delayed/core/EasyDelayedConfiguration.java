package com.bds.easy.delayed.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.bds.easy.delayed.scheduler.StandScheduler;
import com.bds.easy.delayed.store.JDBCDelayedStore;
import com.bds.easy.delayed.store.MemoryDelayedStore;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 10:43
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
@Configuration
public class EasyDelayedConfiguration{

    @Bean
    @ConditionalOnMissingBean(DelayedStore.class)
    @ConditionalOnProperty(prefix = "easy.delayed", name = "store", havingValue = "memory", matchIfMissing = true)
    public MemoryDelayedStore memoryDelayedStore(){
        return new MemoryDelayedStore();
    }

    @Bean
    @ConditionalOnMissingBean(DelayedStore.class)
    @ConditionalOnProperty(prefix = "easy.delayed", name = "store", havingValue = "jdbc", matchIfMissing = false)
    public JDBCDelayedStore jdbcDelayedStore(){return new JDBCDelayedStore(this.initDataSource());}

    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource initDataSource(){
        return new DruidDataSource();
    }
    @Bean
    @ConditionalOnMissingBean(Scheduler.class)
    public Scheduler scheduler(DelayedStore delayedStore, List<Listener> listeners, List<Plugin> plugins) throws SchedulerException{
        StandScheduler scheduler = new StandScheduler(delayedStore);
        scheduler.addListeners(listeners);
        scheduler.addPlugins(plugins);
        scheduler.start();
        return scheduler;
    }
}
