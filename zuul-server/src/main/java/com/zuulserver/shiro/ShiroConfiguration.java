package com.zuulserver.shiro;

import com.zuulserver.dto.Permissions;
import com.zuulserver.service.LoginServiceImpl;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${redis.session.prefix}")
    private String sessionPrefix;

    //将自己的验证方式加入容器
    @Bean
    public MyRealm MyRealm() {
        return new MyRealm();
    }

    @Bean
    public RedisSessionDao RedisSessionDao() {
        return new RedisSessionDao();
    }

    @Autowired
    private LoginServiceImpl loginService;

    //权限管理，配置主要是Realm的管理认证
    @Bean
    @DependsOn("MyRealm")
    public SecurityManager securityManager() {
        logger.info("shiro已经加载");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(MyRealm());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    //自定义sessionManager
    @Bean(name = "sessionManager")
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();

        //全局会话超时时间（单位毫秒），默认30分钟
        long timeout = 1800000L;

        RedisSessionDao sessionDao = RedisSessionDao();
        sessionDao.setExpireTime(timeout);
        if (sessionPrefix != null) {
            sessionDao.setSessionPrefix(sessionPrefix);
        }

        mySessionManager.setSessionDAO(sessionDao);

        mySessionManager.setGlobalSessionTimeout(timeout);
        //是否开启删除无效的session对象  默认为true
        mySessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        mySessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        mySessionManager.setSessionValidationInterval(3600000L);
        //取消url 后面的 JSESSIONID
        mySessionManager.setSessionIdUrlRewritingEnabled(false);
        mySessionManager.setSessionIdCookieEnabled(false);
        return mySessionManager;
    }

    //Filter工厂，设置对应的过滤条件和跳转条件
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        MyShiroFilterFactoryBean shiroFilterFactoryBean = new MyShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        HashMap<String, Filter> hashMap = new HashMap<String, Filter>();
        hashMap.put("authc", new NoLoginFilter());
        hashMap.put("perms", new NoAuthFilter());

        shiroFilterFactoryBean.setFilters(hashMap);

        List<Permissions> sysMenus = loginService.getAllPermissions();
        Map<String, String> filterMap = UpdatePermsUtil.getPermissionMap(sysMenus);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * ehcache缓存管理器；shiro整合ehcache：
     * 通过安全管理器：securityManager
     * 单例的cache防止热部署重启失败
     *
     * @return EhCacheManager
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehcache = new EhCacheManager();
        CacheManager cacheManager = CacheManager.getCacheManager("shiro");
        if (cacheManager == null) {
            try {
                cacheManager = CacheManager.create(ResourceUtils.getInputStreamForPath("classpath:ehcache.xml"));
            } catch (CacheException | IOException e) {
                e.printStackTrace();
            }
        }
        ehcache.setCacheManager(cacheManager);
        return ehcache;
    }

    @Bean
    public CustomHashedCredentialsMatcher customHashedCredentialsMatcher() {
        CustomHashedCredentialsMatcher customHashedCredentialsMatcher = new CustomHashedCredentialsMatcher();
        customHashedCredentialsMatcher.setHashAlgorithmName("md5");// 散列算法:这里使用MD5算法;
        customHashedCredentialsMatcher.setHashIterations(1);// 散列的次数，比如散列两次，相当于md5(md5(""));
        customHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);// 表示是否存储散列后的密码为16进制，需要和生成密码时的一样，默认是base64；
        return customHashedCredentialsMatcher;
    }
}
