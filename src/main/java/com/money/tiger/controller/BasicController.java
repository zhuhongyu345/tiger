package com.money.tiger.controller;

import com.money.tiger.entity.StockBasic;
import com.money.tiger.entity.http.PageQueryReq;
import com.money.tiger.service.BasicService;
import com.money.tiger.service.FlushService;
import com.money.tiger.service.HistoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class BasicController {

    @Resource
    private BasicService basicService;
    @Resource
    private HistoryService historyService;
    @Resource
    private FlushService flushService;

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("search")
    public List<StockBasic> search(PageQueryReq req) {
        return basicService.search(req);
    }

    @PostMapping("addOne")
    public String addOne(String name, Integer type) {
        return basicService.addOne(name, type);
    }

    @PostMapping("tagOne")
    public String tagOne(String id, Integer tag) {
        return basicService.tagOne(id, tag);
    }

    @PostMapping("deleteOne")
    public String deleteOne(String id) {
        return basicService.deleteOne(id);
    }

    @GetMapping("history")
    public Map<String, Object> history(String name, Integer count, String period) {
        return historyService.getHistory(name.toUpperCase(), count, period);
    }

    @PostMapping("flush")
    public String flush(Integer hard, Integer type) {
        return flushService.flush(hard, type);
    }

}
