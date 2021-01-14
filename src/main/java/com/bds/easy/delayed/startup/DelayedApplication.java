package com.bds.easy.delayed.startup;

import com.bds.easy.delayed.core.EnabledDelayedAutoConfiguration;
import com.bds.easy.delayed.listener.LogListenerr;
import com.bds.easy.delayed.mapper.DelayedMapper;
import com.bds.easy.delayed.plugin.LogPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 16:05
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
@Import({LogListenerr.class, LogPlugin.class})
@SpringBootApplication
@EnabledDelayedAutoConfiguration
@MapperScan(basePackageClasses = {DelayedMapper.class})
public class DelayedApplication{
    public static void main(String[] args)throws Exception{
        SpringApplication.run(DelayedApplication.class,args);
    }
}
