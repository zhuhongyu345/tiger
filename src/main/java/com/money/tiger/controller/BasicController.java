package com.money.tiger.controller;

import com.money.tiger.entity.StockBasic;
import com.money.tiger.entity.http.PageQueryReq;
import com.money.tiger.service.BasicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class BasicController {

    @Resource
    private BasicService basicService;

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("search")
    public List<StockBasic> search(PageQueryReq req) {
        return basicService.search(req);
    }

    @PostMapping("addOne")
    public void addOne(String name, Integer type) {
        basicService.addOne(name, type);
    }

    @PostMapping("tagOne")
    public void tagOne(String id, Integer tag) {
        basicService.tagOne(id, tag);
    }

    @PostMapping("deleteOne")
    public void deleteOne(String id) {
        basicService.deleteOne(id);
    }


}
