package com.money.tiger.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.money.tiger.dao.ConfigRepository;
import com.money.tiger.entity.SingleConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class XqProxy {

    @PostConstruct
    private void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                flushToken();
            }
        }, 0, 3600 * 1000L);
    }

    private String token;

    @Resource
    private ConfigRepository configRepository;

    private void flushToken() {
        SingleConfig xueqiuToken = configRepository.findByKey("xueqiu_token");
        if (xueqiuToken != null && xueqiuToken.getValue() != null) {
            token = xueqiuToken.getValue();
        }
    }

    public List<XQKline> getKline(String name, String period, int count) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/chart/kline.json?" +
                        "symbol=%s&begin=%s&period=%s&type=before&count=-%s" +
                        "&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance",
                name, Instant.now().toEpochMilli(), period, count);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .addHeader("Cookie", token)
                .build();
        ArrayList<XQKline> resp = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            JSONObject root = JSONObject.parseObject(string);
            JSONObject data = root.getJSONObject("data");
            JSONArray items = data.getJSONArray("item");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (int i = 0; i < items.size(); i++) {
                JSONArray item = items.getJSONArray(i);
                XQKline k = new XQKline();
                k.setTimestamp(item.getLong(0));
                k.setVolume(item.getFloat(1));
                k.setOpen(item.getFloat(2));
                k.setHigh(item.getFloat(3));
                k.setLow(item.getFloat(4));
                k.setHigh(item.getFloat(5));
                k.setChg(item.getFloat(6));
                k.setPercent(item.getFloat(7));
                k.setPe(item.getFloat(12));
                k.setTime(sdf.format(new Date(k.getTimestamp())));
                resp.add(k);
            }
            return resp;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
