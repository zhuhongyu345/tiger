package com.money.tiger.service;

import com.money.tiger.biz.XQKline;
import com.money.tiger.biz.XqProxy;
import com.money.tiger.entity.data.ZCPoint;

import java.util.ArrayList;
import java.util.List;

public class ZhiChengUtil {

    public static Float getZhiCheng(List<XQKline> klines, float diffPercent) {
        if (klines == null || klines.isEmpty()) {
            return -1F;
        }
        ArrayList<Float> lows = new ArrayList<>();
        for (XQKline kline : klines) {
            lows.add(kline.getLow());
        }
        List<ZCPoint> zhicheng = new ArrayList<>();
        Float temp = 100000F;
        boolean up = true;
        Float maxPrice = 0F;
        Float minPrice = 10000F;
        for (XQKline kline : klines) {
            //找支撑位
            if (kline.getLow() < temp) {
                up = false;
            } else {
                if (!up) {
                    ZCPoint zcPoint = new ZCPoint();
                    zcPoint.setPrice(temp);
                    zcPoint.setEffect(0.1F);
                    zhicheng.add(zcPoint);
                }
                up = true;
            }
            temp = kline.getLow();
            //找最高最低价
            if (kline.getLow() < minPrice) {
                minPrice = kline.getLow();
            }
            if (kline.getHigh() > maxPrice) {
                maxPrice = kline.getHigh();
            }
        }

        for (ZCPoint zcPoint : zhicheng) {
            float v = (maxPrice - zcPoint.getPrice()) / (maxPrice - minPrice);
            zcPoint.setEffect(v * v);
        }
        for (ZCPoint i : zhicheng) {
            Float orgEff = i.getEffect();
            Float maxEff = orgEff;
            for (ZCPoint j : zhicheng) {
                if (Math.abs(i.getPrice() - j.getPrice()) / i.getPrice() < diffPercent) {
                    maxEff += orgEff;
                }
            }
            i.setEffect(maxEff);
        }
        Float closePrice = klines.get(klines.size() - 1).getClose();
        Float zhichengLv = 0f;
        for (ZCPoint zcPoint : zhicheng) {
            float diff = Math.abs(closePrice - zcPoint.getPrice());
            if (diff < closePrice * diffPercent) {
                zhichengLv += zcPoint.getEffect();
            }
        }
        return zhichengLv;
    }

    public static void main(String[] args) throws Exception {
        //	kData, _, _ := getKlineFromXQ("JKHY", "day", 69)
        XqProxy xqProxy = new XqProxy();
        xqProxy.flushToken("ssxmod_itna=iq+ODK4RrhkYG=G0FTcDQI4GTqYqGHDyxWKG7DuxiK08D6BxB40Q2zfGe=qiDcBCiht4eDxo5D/YnqeDZDGIdDqx0oiUKjgitY=tnwA4pz8YQv4N5WziLRtqW+I08SdbXFdAiDU4i8DCkIibqeD4+3Dt4DIDAYDDxDWieGyIeGuDG=bDGP5x03DfqTFmBN5oGI7tYbDIbm/D0QDAwcvmiPbDit5xGYbD0U/0GmFxcmIn1bDboRqDDXq+/DEtGHKaFgt1=F86Py1aoy//aHpr+RdcnrDbSNQ+obnumepcqtNKDm6l7qiSrEOiG=ChqQqYmXOQwAcw=jxYe6Cjx1nGKDxPDh+Wm6jpwDDAn5YyRDeeYmvS8yMDrVQDbWG8IuC947tGHAox9bKyhND48IxqZ5qtB5SrY9v4eD; cookiesu=511746597094136; device_id=3eb27fa0dbf1f9e4a7d308d9f6ba6f85; acw_tc=1a0c63d517465970939672284e007264f42a8d3df4d5c6b5085441aaed632f; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1746597092; xq_r_token=8381269ec9b02e5fc66996c2868a95087d02ea0e; Hm_lvt_1db88642e346389874251b5a1eded6e3=1746597092; HMACCOUNT=776A040A4E26F737; .thumbcache_f24b8bbe5a5934237bbc0eda20c1b6e7=qgu3Rao6Jw/f1crcBbvkKpN1rgK/KsfCvLVcsHQZGps/sLwkDVRqO3/bY0Wv9zavApflVH8wPxGzFlAyk+cNtQ%3D%3D; xqat=a9afe36f1d53c5f7180395537db631d013033091; xq_a_token=a9afe36f1d53c5f7180395537db631d013033091; u=511746597094136; ssxmod_itna2=iq+ODK4RrhkYG=G0FTcDQI4GTqYqGHDyxWKG7DuxiK08D6BxB40Q2zfGe=qiDcBCiht4eDxxeDAD1DnRItORjAtzV8enujzkG9ZGD; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTc0ODY1Mzc0OSwiY3RtIjoxNzQ2NTk3MDU4Mzc4LCJjaWQiOiJkOWQwbjRBWnVwIn0.jjvZOu922YlDkRJfoQU3V_M6mk-MM9jFGxi3rBDVClOANfEWRJ-LW0usNgOVy4YAmvlYC74FsTAes-f0w3WYgCif4BZ-5IhI0zYvumlwIIruXmEfnXqZTx4Qy7pwIhWY4DRk5phK2Ed7-79moTrfmRcLkV5AwjGCBgy-WrY4goS66L7q1MBO-pqqLb2YJEXyV76u9Sqt9qblbXBf0NhGzbjfPaI_ose_W0zcmfQjI59n0VKCOz-gmbnUQCLYCpc_DyF3vIqDJLlRhd0ee0qmvsi1UNN6yIAKcoF5nmf7UVbkJBlJx5SB7bNMFA1Dp-vduFc0yrv0BwJ-6kbOJP2ooA; smidV2=20250507135132f400463dc5d3b4591c23fbd79e4d360c008be6baf13c69410; ");
        List<XQKline> kline = xqProxy.getKline("JKHY", "day", 69);
        Float zhiCheng = getZhiCheng(kline, 0.009F);
        System.out.println(zhiCheng);
        System.out.println(zhiCheng);

    }
}
