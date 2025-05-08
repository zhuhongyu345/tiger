package com.money.tiger.entity.http;

import lombok.Data;

@Data
public class PageQueryReq {
    private Float zclHigh;
    private Float zclLow;
    private Float cjlLow;
    private Float cjlHigh;
    private Float hlLow;
    private Float hlHigh;
    private Float peHigh;
    private Float peLow;
    private String name;
    private Float yield;
    private Integer type;
    private Long skip;
    private Integer size;
    private String sort;
    private String sortType;

}