package com.money.tiger.entity.http;

import lombok.Data;

@Data
public class PageQueryReq {
    private String zclHigh;
    private String zclLow;
    private String cjlLow;
    private String cjlHigh;
    private String hlLow;
    private String hlHigh;
    private String peHigh;
    private String peLow;
    private String name;
    private String yield;
    private String tpe;
    private Long skip;
    private Integer size;
    private String sort;
    private String sortType;

}