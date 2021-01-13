package com.bds.easy.delayed.core;

import com.bds.easy.delayed.api.DelayedController;
import com.bds.easy.delayed.scheduler.StandScheduler;
import com.bds.easy.delayed.store.JDBCDelayedStore;
import com.bds.easy.delayed.store.MemoryDelayedStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public JDBCDelayedStore jdbcDelayedStore(){return new JDBCDelayedStore();}

    @Bean
    @ConditionalOnMissingBean(SchedulerThread.class)
    public SchedulerThread threadPoolExecutor(DelayedStore store){
        return new SchedulerThread(new ThreadPoolExecutor(1 , 1 , 0L , TimeUnit.MILLISECONDS , new LinkedBlockingQueue<>()),store);
    }

    @Bean
    @ConditionalOnMissingBean(Scheduler.class)
    public Scheduler scheduler(SchedulerThread thread, List<Listener> listeners, List<Plugin> plugins) throws SchedulerException{
        StandScheduler scheduler = new StandScheduler(thread);
        scheduler.addListeners(listeners);
        scheduler.addPlugins(plugins);
        scheduler.start();
        return scheduler;
    }
}
