package com.provider1st.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("provider2")
public interface Provider2Client {

    @GetMapping(value = "/test/get")
    public String provider2Test();

    @PostMapping(value = "/test/param/{param1}")
    public String provider2Param(@PathVariable(value = "param1") String param1);
}
