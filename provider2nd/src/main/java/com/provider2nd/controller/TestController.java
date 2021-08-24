package com.provider2nd.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("get")
    public String get() {
        return "this is provider 2";
    }


    @PostMapping("param/{param1}")
    public String param(@PathVariable(value = "param1") String param1) {
        return "provider2 get " + param1;
    }
}
