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
    private String mic;
    private int type;
    private String chn;
    private Float yield;
    private Float pe;
    private Float pef;
    private Float peg;
    private Float price;
    private Float h52;
    private Float l52;
    private Float hl;
    private Float liangbi;
    private Float shizhi;
    private Float huanshoulv;
    private Float cjlrateday;
    private Float zcrate;
    private Float zcweek;
    private int tag;
    private String up;

}
