package com.money.tiger.dao;

import com.money.tiger.entity.StockBasic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockBasicRepository extends MongoRepository<StockBasic, String> {

    StockBasic findByName(String name);

}
