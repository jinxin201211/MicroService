package com.zuulserver.shiro;

import com.zuulserver.dto.Permissions;
import com.zuulserver.dto.User;
import com.zuulserver.service.LoginServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyRealm extends AuthorizingRealm {

    static Logger logger = LoggerFactory.getLogger(MyRealm.class);

    @Autowired
    private LoginServiceImpl loginService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String loginName = (String) principalCollection.getPrimaryPrincipal();
        User user = loginService.getUserByName(loginName);
        List<String> permissions = new ArrayList<String>();
        List<String> roles = new ArrayList<String>();//角色待处理
        List<Permissions> sysMenus = loginService.getPermissions(user.getUserName());
        for (Permissions item : sysMenus) {
            permissions.add("/" + item.getPermissionsName());
        }
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        authInfo.addStringPermissions(permissions);
        authInfo.addRoles(roles);
        return authInfo;
    }

    //验证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CustomUsernamePasswordToken upToken = (CustomUsernamePasswordToken) token;
        if (upToken.getType().equals(LoginType.PASSWORD)) {
            //UsernamePasswordToken upToken = (UsernamePasswordToken) token;
            String loginName = upToken.getUsername();
            String pwd = String.valueOf(upToken.getPassword());
            User user = loginService.getUserByName(loginName);
            //由shiro完成密码比对
            if (pwd.equals(user.getPassword())) {
                Object principal = loginName;
                Object credentitals = pwd;

                //处理session
                DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
                DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
                RedisSessionDao redisSessionDao = (RedisSessionDao) sessionManager.getSessionDAO();
                Collection<Session> sessions = redisSessionDao.getActiveSessions();//获取当前已登录的用户session列表
                for (Session session : sessions) {
                    //清除该用户以前登录时保存的session
                    if (loginName.equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))) {
                        redisSessionDao.delete(session);
                        redisSessionDao.setKickoutSession(session);
                    }
                }

                return new SimpleAuthenticationInfo(principal, credentitals, getName());

            } else {
                return null;
            }
        } else {
            String sfzmhm = upToken.getUsername();
            Object principal = sfzmhm;
            //处理session
            DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
            DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
            RedisSessionDao redisSessionDao = (RedisSessionDao) sessionManager.getSessionDAO();
            Collection<Session> sessions = redisSessionDao.getActiveSessions();//获取当前已登录的用户session列表
            for (Session session : sessions) {
                //清除该用户以前登录时保存的session
                if (sfzmhm.equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))) {
                    redisSessionDao.delete(session);
                    redisSessionDao.setKickoutSession(session);
                }
            }
            return new SimpleAuthenticationInfo(principal, "", getName());
        }
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }
}
