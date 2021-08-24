package com.provider1st.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("get")
    public Object test() {
        return "this is provider 1";
    }

    @GetMapping("date")
    public Object date(Date date) {
        System.out.println(date);
        return date;
    }
}
