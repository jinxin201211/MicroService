package com.zuulserver.shiro;

import com.zuulserver.dto.Permissions;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @Author: liaoph
 * @Date: 2019/4/23 10:43
 * @Version 1.0
 */
public class UpdatePermsUtil {

    static Logger logger = LoggerFactory.getLogger(UpdatePermsUtil.class);

    /**
     * 动态更新新的权限
     *
     * @param filterMap
     */
    public synchronized static void updatePermission(ShiroFilterFactoryBean myShiroFilterFactoryBean, Map<String, String> filterMap) {

        AbstractShiroFilter shiroFilter = null;
        try {
            shiroFilter = (AbstractShiroFilter) myShiroFilterFactoryBean.getObject();

            // 获取过滤管理器
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                    .getFilterChainResolver();
            DefaultFilterChainManager filterManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            //清空拦截管理器中的存储
            filterManager.getFilterChains().clear();
            /*
            清空拦截工厂中的存储,如果不清空这里,还会把之前的带进去
            ps:如果仅仅是更新的话,可以根据这里的 map 遍历数据修改,重新整理好权限再一起添加
             */
            myShiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

            // 相当于新建的 map, 因为已经清空了
            Map<String, String> chains = myShiroFilterFactoryBean.getFilterChainDefinitionMap();
            //把修改后的 map 放进去
            chains.putAll(filterMap);

            //这个相当于是全量添加
            for (Map.Entry<String, String> entry : filterMap.entrySet()) {
                //要拦截的地址
                String url = entry.getKey().trim().replace(" ", "");
                //地址持有的权限
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                //生成拦截
                filterManager.createChain(url, chainDefinition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("updatePermission error,filterMap=" + filterMap, e);
        }

        myShiroFilterFactoryBean.getFilterChainDefinitionMap();
    }

    public static Map<String, String> getPermissionMap(List<Permissions> sysMenus) {
        Map<String, String> filterMap = new LinkedHashMap<>();

        filterMap.put("/login", "anon");

        for (Permissions item : sysMenus) {
            filterMap.put("/" + item.getPermissionsName(), "perms[/" + item.getPermissionsName() + "]");
        }
        //对所有用户认证
        filterMap.put("/**", "authc");

        return filterMap;
    }

}
