package com.zuulserver.comm;


import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


public class ResponseUtil {

    /**
     * data输出为json
     */
    public static void writeJson(HttpServletResponse response, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(jsonObj.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}

