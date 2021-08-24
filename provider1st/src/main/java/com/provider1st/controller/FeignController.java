package com.provider1st.controller;

import com.provider1st.client.Provider2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign")
public class FeignController {
    @Autowired
    public Provider2Client provider2Client;

    @RequestMapping("provider2")
    public Object provider2() {
        return provider2Client.provider2Param("Hello Feign");
    }
}
