import com.alibaba.fastjson.JSONArray;
import com.money.tiger.biz.nsdq.NsdqOne;
import com.money.tiger.biz.webull.WBProxy;
import okhttp3.*;

public class TestWN {

    public static void main(String[] args) throws Exception {
        importSome(2, 600, 1);
        importSome(1, 1000, 2);
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
        WBProxy proxy = new WBProxy();
        for (int i = 0; i < objects.size(); i++) {
            NsdqOne object = objects.getObject(i, NsdqOne.class);
            String micCode = object.getMicCode();
            String symbolTicker = object.getSymbolTicker();

            String basic = proxy.getBasic(symbolTicker, micCode);
            System.out.println(symbolTicker + ":"+i+ ":" + basic);
        }
        System.out.println("import page:" + page + ",size:" + objects.size());
        return objects.size();
    }

}
