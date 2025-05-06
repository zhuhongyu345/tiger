package com.money.tiger.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "config")
public class SingleConfig {


    @Id
    private String id;
    //xueqiu
    private String key;
    //ms
    private String value;


}
