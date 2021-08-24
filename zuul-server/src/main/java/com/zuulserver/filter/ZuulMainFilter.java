package com.zuulserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ZuulMainFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        URL url = null;
        try {
            url = new URL(request.getRequestURL().toString());
            String path = url.getPath();
/*            if (path.split("/")[1].equals("provider1")) {
                ctx.setSendZuulResponse(true);  //true-进行路由
            } else {
                ctx.setSendZuulResponse(false);  //true-进行路由
                ResponseUtil.writeJson(response, new Response(ResultCodeEnum.ServiceDisable).toJsonString());
            }*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
