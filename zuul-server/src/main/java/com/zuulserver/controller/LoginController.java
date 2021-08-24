package com.zuulserver.controller;

import com.zuulserver.comm.Response;
import com.zuulserver.dto.User;
import com.zuulserver.service.LoginServiceImpl;
import com.zuulserver.shiro.CustomUsernamePasswordToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoginServiceImpl loginService;

    @GetMapping("/login")
    public Response login(User user) {
//        Set<String> keys = redisTemplate.keys("*");
//        for (String key : keys) {
//            redisTemplate.delete(key);
//        }
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
            return new Response().error("请输入用户名和密码！");
        }
        //用户认证信息
        Subject subject = SecurityUtils.getSubject();
        String JSESSIONID = subject.getSession().getId().toString();
        CustomUsernamePasswordToken loginToken = new CustomUsernamePasswordToken(user.getUserName(), user.getPassword().toString());
//        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUserName(), user.getPassword());
        try {
            //进行验证，这里可以捕获异常，然后返回对应信息
            subject.login(loginToken);
        } catch (UnknownAccountException e) {
            logger.error("用户名不存在！", e);
            return new Response().error("用户名不存在！");
        } catch (AuthenticationException e) {
            logger.error("账号或密码错误！", e);
            return new Response().error("账号或密码错误！");
        } catch (AuthorizationException e) {
            logger.error("没有权限！", e);
            return new Response().error("没有权限");
        }
        return new Response().success(JSESSIONID, "登录成功！");
    }

    @RequestMapping("/logout")
    public Response logout() {
        Subject subject = SecurityUtils.getSubject();
        String loginName = (String) subject.getPrincipal();
        loginService.clearCache(loginName);
        subject.logout();
        return new Response().success(null, "退出成功！");
    }

    @GetMapping("/noauth")
    public String noauth() {
        return "noauth success!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin success!";
    }

    @GetMapping("/index")
    public String index() {
        return "index success!";
    }

    @GetMapping("/add")
    public String add() {
        return "add success!";
    }
}