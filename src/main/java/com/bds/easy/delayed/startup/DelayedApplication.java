package com.bds.easy.delayed.startup;

import com.bds.easy.delayed.core.EnableEasyDelayed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2020/12/29 16:05
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2020/12/29
 */
@SpringBootApplication
@EnableEasyDelayed
public class DelayedApplication{
    public static void main(String[] args){
        SpringApplication.run(DelayedApplication.class,args);
    }
}
