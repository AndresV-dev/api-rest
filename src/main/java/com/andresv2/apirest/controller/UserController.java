package com.andresv2.apirest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "user/")
public class UserController {

    @RequestMapping(value = "hello")
    public String sayHello(){
        return "Hello User";
    }
}
