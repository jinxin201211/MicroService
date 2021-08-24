package com.zuulserver.shiro;

import com.alibaba.fastjson.JSONObject;
import com.zuulserver.comm.Response;
import com.zuulserver.comm.ResultCodeEnum;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;


public class NoLoginFilter extends FormAuthenticationFilter {

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     *
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;


        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        MySessionManager sessionManager = (MySessionManager) securityManager.getSessionManager();
        RedisSessionDao redisSessionDao = (RedisSessionDao) sessionManager.getSessionDAO();

        Serializable SessionId = sessionManager.getSessionId(request, response);

        Session kickoutSession = redisSessionDao.getKickoutSession(SessionId);

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/json");

        if (kickoutSession == null) {
            Response res = new Response(ResultCodeEnum.NoLogin);
            httpServletResponse.getWriter().write(JSONObject.toJSON(res).toString());
        } else {
            redisSessionDao.deleteKickoutSession(kickoutSession);
            Response res = new Response(ResultCodeEnum.KickOut);
            httpServletResponse.getWriter().write(JSONObject.toJSON(res).toString());
        }
        return false;
    }

    private boolean isAjax(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}

