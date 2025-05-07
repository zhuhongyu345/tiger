package com.money.tiger.biz;

import lombok.Data;

@Data
public class XQKline {
    private long timestamp;
    private Float volume;
    private Float open;
    private Float high;
    private Float low;
    private Float close;
    private Float chg;//涨额
    private Float percent;//涨幅
    private Float pe;
    private String time;
}
