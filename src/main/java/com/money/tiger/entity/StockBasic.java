package com.money.tiger.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

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
    private BigDecimal yield;
    private BigDecimal pe;
    private BigDecimal pef;
    private BigDecimal peg;
    private BigDecimal price;
    private BigDecimal h52;
    private BigDecimal l52;
    private BigDecimal hl;
    private BigDecimal liangbi;
    private BigDecimal shizhi;
    private BigDecimal huanshoulv;
    private BigDecimal cjlrateday;
    private BigDecimal zcrate;
    private BigDecimal zcweek;
    private int tag;
    private String up;

}
