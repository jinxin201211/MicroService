package com.zuulserver.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

import java.util.Map;

/**
 * @description:
 * @Author: liaoph
 * @Date: 2019/4/23 9:39
 * @Version 1.0
 */
public class MyShiroFilterFactoryBean extends ShiroFilterFactoryBean {

    @Override
    public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
        super.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }
}
