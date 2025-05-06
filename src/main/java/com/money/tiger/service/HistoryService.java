package com.money.tiger.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {
    public Map<String, List<Object>> getHistory(String name, String count, String period) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=%s&begin=%s&period=%s" +
                        "&type=before&count=-%s&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance",
                name, Instant.now().toEpochMilli(), period, count);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .addHeader("Cookie", "cookiesu=951743585861401; device_id=2c86f596b3f507ba159147c36f1ae4df; xq_a_token=a9afe36f1d53c5f7180395537db631d013033091; xqat=a9afe36f1d53c5f7180395537db631d013033091; xq_r_token=8381269ec9b02e5fc66996c2868a95087d02ea0e; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTc0ODY1Mzc0OSwiY3RtIjoxNzQ2NTI0MzMxMTczLCJjaWQiOiJkOWQwbjRBWnVwIn0.WCDMHYWkKhqOgCbs_r_1KziQfn5ShDQiDNOl-IpuYPchcQoKweIFbaOrgz9e1_lL_WOZshkYuESNzm-sdaTepXhrtHeIv_cDMfuHqJ5GuGVmanO4aeZ9YHEFyhTr2DC3bjiacQ5aMntnleMaBRQRRB1Xg42e_EFQjGIxq_WQy6F1nBAh4ZVkL-Nb7uuSDRLxj3Tniw02Czw__J5gcweKi7Yfd1Uwy4wNN4ZOisCMqVDdrQxqa9BXYlp9nFKC-h0gt97W-a1c4SMSoJTObqZMT7heX1RV1Cj4FefdPTiNN7VCyDy4jczJU3JeCYGyOkjlb24v3Ydb9yKEWnrzaK593A; u=951743585861401; Hm_lvt_1db88642e346389874251b5a1eded6e3=1746524357; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1746524357; HMACCOUNT=EA8C3BC48396B714; ssxmod_itna=Yq0hDIOt0KBIxYQiQGCDcDQM4YMD3k440dGM/Deq7ttGcD8OD0PGOm5BKPxAhg8756dQYQORYKSD0ymdwDA5Dnzx7YDt=ScteSzCB+23Q5BARrdd7zDxu3E=Nrgxg6TNOZhrMKxB3DExGknQ0YdQ4DxOPD5xDTDWeDGDD354iTDieDjDEpH4oDbPx5sD0kD3oDWaoDED3D7xgi5DoDTZxDRgy5fQgxDGyxxyGqt4Gr6zEsy/xDwxD1nCEri4G1fD0HHCZjDD598s0IHK6rLfm+mb3DvxDksDoDoxYuRQOvLFBt3xnYga0YHGon74eA4MBDnGxMrYeB5QDq5jiHB4=mxej4ogYgrwwDDicNiIv1QhmGUcgUB=NOQ44AqZahgevBm50CD5eQ5C51Aq5nwsemdBDKQGmCh3G21DhsDRxWxxD; ssxmod_itna2=Yq0hDIOt0KBIxYQiQGCDcDQM4YMD3k440dGM/Deq7ttGcD8OD0PGOm5BKPxAhg8756dQYQORYKwDDcAqw8/efTv5ggqAi5QKmCfnI4D")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            JSONObject root = JSONObject.parseObject(string);
            JSONObject data = root.getJSONObject("data");
            JSONArray jsonArray = data.getJSONArray("column");
            int tsPos = 0;
            int cloPos = 0;
            int pePos = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                String va = jsonArray.getString(i);
                if (va.equals("timestamp")) {
                    tsPos = i;
                } else if (va.equals("close")) {
                    cloPos = i;
                } else if (va.equals("pe")) {
                    pePos = i;
                }
            }
            JSONArray datas = data.getJSONArray("item");
            for (int i = 0; i < datas.size(); i++) {

            }


        } catch (IOException e) {

        }

        return null;
    }
}
