package com.money.tiger.biz.xq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.money.tiger.biz.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class XqProxy {

    private String token;

    public void flushToken(String token) {
        this.token = token;
    }

    public List<XQKline> getKline(String name, String period, int count) {
        if (token == null) {
            return null;
        }
        String url = String.format("https://stock.xueqiu.com/v5/stock/chart/kline.json?" +
                        "symbol=%s&begin=%s&period=%s&type=before&count=-%s" +
                        "&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance",
                name, Instant.now().toEpochMilli(), period, count);

        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        headers.put("Cookie", token);
        String string = HttpUtil.call(url, null, null, headers, "GET");
        JSONObject root = JSONObject.parseObject(string);

        ArrayList<XQKline> resp = new ArrayList<>();
        JSONObject data = root.getJSONObject("data");
        JSONArray items = data.getJSONArray("item");
        if (items == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 0; i < items.size(); i++) {
            JSONArray item = items.getJSONArray(i);
            XQKline k = new XQKline();
            k.setTimestamp(item.getLong(0));
            k.setVolume(item.getFloat(1));
            k.setOpen(item.getFloat(2));
            k.setHigh(item.getFloat(3));
            k.setLow(item.getFloat(4));
            k.setClose(item.getFloat(5));
            k.setChg(item.getFloat(6));
            k.setPercent(item.getFloat(7));
            k.setPe(item.getFloat(12));
            k.setTime(sdf.format(new Date(k.getTimestamp())));
            resp.add(k);
        }
        return resp;
    }


    public XQDetail getDetail(String name) {
        if (token == null) {
            return null;
        }
        String url = String.format("https://stock.xueqiu.com/v5/stock/quote.json?symbol=%s&extend=detail", name);
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        headers.put("Cookie", token);
        String string = HttpUtil.call(url, null, null, headers, "GET");
        if (string == null) {
            return null;
        }
        JSONObject root = JSONObject.parseObject(string);
        JSONObject quote = root.getJSONObject("data").getJSONObject("quote");
        return quote == null ? null : quote.toJavaObject(XQDetail.class);
    }
}
