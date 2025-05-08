package com.money.tiger.biz.nsdq;

import lombok.Data;

@Data
public class NsdqOne {
    private int total;

    private String url;

    private String exchangeId;

    private String instrumentType;

    private String symbolTicker;

    private String symbolExchangeTicker;

    private String normalizedTicker;

    private String symbolEsignalTicker;

    private String instrumentName;

    private String micCode;

}