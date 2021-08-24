package com.zuulserver.shiro;


import com.alibaba.fastjson.JSONObject;
import com.zuulserver.comm.Response;
import com.zuulserver.comm.ResultCodeEnum;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoAuthFilter extends PermissionsAuthorizationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/json");
        Response res = new Response(ResultCodeEnum.UnAuth);
        httpServletResponse.getWriter().write(JSONObject.toJSON(res).toString());
        return false;
    }

}
