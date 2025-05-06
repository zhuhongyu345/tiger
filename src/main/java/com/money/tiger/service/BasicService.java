package com.money.tiger.service;

import com.alibaba.fastjson.JSONObject;
import com.money.tiger.dao.StockBasicRepository;
import com.money.tiger.entity.StockBasic;
 import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BasicService {


    @Resource
    private StockBasicRepository basicRepository;

    public JSONObject search() {
        return null;
    }


    public void addOne(String name, Integer type) {
        StockBasic byName = basicRepository.findByName(name);
        if (byName != null || type == null) {
            return;
        }
        basicRepository.save(new StockBasic().setName(name).setType(type).setTag(1));
    }
}
