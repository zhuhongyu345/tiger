package com.money.tiger.biz;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class HttpUtil {

    public static String call(String url, Map<String, Object> param, String body, Map<String, String> headers, String method) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (body != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            if (param != null) {
                StringBuilder formData = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        formData.append("&");
                    }
                    formData.append(entry.getKey());
                    formData.append("=");
                    formData.append(entry.getValue());
                }
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                byte[] postData = formData.toString().getBytes(StandardCharsets.UTF_8);
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(postData);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                log.error("请求失败，响应状态码: {} ", responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            log.error("{} err", url, e);
        }
        return null;
    }


}
