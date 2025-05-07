package com.money.tiger.biz;

import lombok.Data;

@Data
public class XQKline {
    private long timestamp;
    private float volume;
    private float open;
    private float high;
    private float low;
    private float close;
    private float chg;//涨额
    private float percent;//涨幅
    private float pe;
    private String time;
}
