package com.money.tiger.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stock")
@Accessors(chain = true)
public class StockBasic {

    @Id
    private String id;
    private String name;
    private int type;
    private String chn;
    private float yield;
    private float pe;
    private float price;
    private float h52;
    private float l52;
    private float hl;
    private float liangbi;
    private float shizhi;
    private float huanshoulv;
    private float cjlrateday;
    private float zcrate;
    private float zcweek;
    private int tag;
    private String up;

}
