package com.bds.easy.delayed.core;

import com.bds.easy.delayed.api.DelayedController;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/08 10:49
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/08
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EasyDelayedConfiguration.class, DelayedController.class})
public @interface EnableEasyDelayed{}
