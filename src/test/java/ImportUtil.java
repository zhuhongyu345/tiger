import com.alibaba.fastjson.JSONArray;
import com.money.tiger.biz.HttpUtil;
import com.money.tiger.biz.nsdq.NsdqOne;
import okhttp3.*;

import java.util.HashMap;

public class ImportUtil {

    public static void main(String[] args) throws Exception {
        int i = 0;
        while (true) {
            i++;
            int res = importSome(i, 10000, 1);
            if (res == 0) {
                break;
            }
        }

        int j=0;
        while (true){
            j++;
            int res = importSome(j, 10000, 2);
            if (res == 0) {
                break;
            }
        }

    }

    public static int importSome(int page, int size, int type) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        String ty = "EQUITY";
        if (type == 2) {
            ty = "EXCHANGE_TRADED_FUND";
        }
        RequestBody body = RequestBody.create(mediaType, "{\"instrumentType\":\"" + ty + "\",\"pageNumber\":" + page + "," +
                "\"sortColumn\":\"NORMALIZED_TICKER\",\"sortOrder\":\"ASC\",\"maxResultsPerPage\":" + size + ",\"filterToken\":\"\"}");
        Request request = new Request.Builder()
                .url("https://www.nyse.com/api/quotes/filter")
                .method("POST", body)
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .build();
        Response response = client.newCall(request).execute();
        JSONArray objects = JSONArray.parseArray(response.body().string());
        for (int i = 0; i < objects.size(); i++) {
            NsdqOne object = objects.getObject(i, NsdqOne.class);
            String micCode = object.getMicCode();
            String symbolTicker = object.getSymbolTicker();
            HashMap<String, Object> param = new HashMap<>();
            param.put("name", symbolTicker);
            param.put("mic", micCode);
            param.put("type", type);
            HttpUtil.call("http://127.0.0.1:9347/addOne", param, null, null, "POST");
        }
        System.out.println("import page:"+page+",size:"+objects.size());
        return objects.size();
    }

}
