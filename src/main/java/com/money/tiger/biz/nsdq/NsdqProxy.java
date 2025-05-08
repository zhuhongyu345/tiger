package com.money.tiger.biz.nsdq;

import com.alibaba.fastjson.JSONArray;
import com.money.tiger.biz.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class NsdqProxy {

    public NsdqOne getOne(String code) {
        String url = "https://www.nyse.com/api/quotes/filter";
        String body = "{\"instrumentType\":\"EQUITY\",\"pageNumber\":1,\"sortColumn\":\"NORMALIZED_TICKER\"," +
                "\"sortOrder\":\"ASC\",\"maxResultsPerPage\":10,\"filterToken\":\"" + code + "\"}";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Sec-Fetch-Site", "same-origin");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "\"Windows\"");

        String resp = HttpUtil.call(url, null, body, headers, "POST");
        JSONArray objects = JSONArray.parseArray(resp);
        if (!objects.isEmpty()) {
            return objects.getObject(0, NsdqOne.class);
        }
        return null;
    }


}
