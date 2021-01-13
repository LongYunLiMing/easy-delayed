package com.bds.easy.delayed.core;

/**
 * description:
 *
 * @author deli.yang@hand-china.com
 * @date 2021/01/07 13:56
 * @lastUpdateBy: deli.yang@hand-china.com
 * @lastUpdateDate: 2021/01/07
 */
@FunctionalInterface
public interface Function<T>{
    void deal(T t);
}
