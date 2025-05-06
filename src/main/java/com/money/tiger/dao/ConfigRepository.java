package com.money.tiger.dao;

import com.money.tiger.entity.SingleConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepository extends MongoRepository<SingleConfig, String> {

    SingleConfig findByKey(String key);

}
