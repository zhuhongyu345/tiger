package com.money.tiger.controller;

import com.money.tiger.service.BasicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BasicController {

    @Resource
    private BasicService basicService;

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("search")
    public String search() {
        return "pong";
    }

    @RequestMapping("addOne")
    public String addOne(String name, Integer type) {
        basicService.addOne(name, type);
        return "success";
    }


}
