package com.money.tiger.biz.webull;

import com.money.tiger.biz.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WebullProxy {


    public String getBasic(String code, String mic) {
        //https://quotes-gw.webullfintech.com/api/quotes/ticker/getRealTimeBySymbol?exchangeCode=NYSE&symbol=AACT-U
        //https://quotes-gw.webullfintech.com/api/quotes/ticker/getRealTimeBySymbol?exchangeCode=NASDAQ&symbol=AAL
        String ex = null;
        switch (mic) {
            case "XNYS":
                ex = "NYSE";
                break;
            case "XNGS":
                ex = "NASDAQ";
                break;
            case "XNMS":
                ex = "NASDAQ";
                break;
            case "XNCM":
                ex = "NASDAQ";
                break;
            case "XASE":
                ex = "AMEX";
                break;
            case "ARCX":
                ex = "NYSEARCA";
                break;
           case "BATS":
                ex = "BATS";
                break;
            default:
                throw new RuntimeException(mic + "-" + code);
        }

        String url = "https://quotes-gw.webullfintech.com/api/quotes/ticker/getRealTimeBySymbol?exchangeCode=" + ex + "&symbol=" + code;
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("accept-language", "zh-CN,zh;q=0.9");
        headers.put("cache-control", "max-age=0");
        headers.put("priority", "u=0, i");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "\"Windows\"");
        headers.put("sec-fetch-dest", "document");
        headers.put("sec-fetch-mode", "navigate");
        headers.put("sec-fetch-site", "none");
        headers.put("sec-fetch-user", "?1");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        return HttpUtil.call(url, null, null, headers, "GET");
    }


    public static void main(String[] args) {

        new WebullProxy().getBasic(null, null);
    }
}
