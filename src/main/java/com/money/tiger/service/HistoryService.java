package com.money.tiger.service;

import com.money.tiger.biz.xq.XQKline;
import com.money.tiger.biz.xq.XqProxy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HistoryService {

    @Resource
    private XqProxy xqProxy;

    public Map<String, Object> getHistory(String name, int count, String period) {
        List<XQKline> kline = xqProxy.getKline(name, period, count);
        List<String> times = new ArrayList<>();
        List<Float> pes = new ArrayList<>();
        List<Float> prices = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (XQKline xqKline : kline) {
            times.add(sdf.format(new Date(xqKline.getTimestamp())));
            pes.add(xqKline.getPe() == null ? null : xqKline.getPe());
            prices.add(xqKline.getClose());
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("pes", pes);
        resp.put("prices", prices);
        resp.put("times", times);
        return resp;
    }
}
