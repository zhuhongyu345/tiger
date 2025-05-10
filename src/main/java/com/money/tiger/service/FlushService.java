package com.money.tiger.service;

import com.money.tiger.biz.webull.WBProxy;
import com.money.tiger.biz.xq.XQDetail;
import com.money.tiger.biz.xq.XQKline;
import com.money.tiger.biz.xq.XqProxy;
import com.money.tiger.dao.StockBasicRepository;
import com.money.tiger.entity.StockBasic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FlushService {

    boolean doing = false;

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private StockBasicRepository basicRepository;
    @Resource
    private XqProxy xqProxy;
    @Resource
    private WBProxy wbProxy;

    public String flush(Integer hard, Integer type) {
        if (!doing || type == -1) {
            doing = true;
            new Thread(() -> {
                Criteria criteria = new Criteria();
                if (type != null) {
                    if (type > 0) {
                        criteria.and("type").is(type);
                    } else {
                        criteria.and("tag").is(1);
                    }
                }
                if (hard == null || hard == 0) {
                    criteria.and("up").ne(LocalDate.now().toString());
                }
                List<StockBasic> stockBasics = mongoTemplate.find(new Query(criteria), StockBasic.class);
                int fail = 0;
                for (StockBasic stockBasic : stockBasics) {
                    String resp = flushOne(stockBasic);
                    if (resp != null) {
                        log.warn("one fail:{},{}", stockBasic.getName(), resp);
                        if (++fail > 10) {
                            log.error("fail break:{}", fail);
                            break;
                        }
                    }
                }
                doing = false;
            }).start();
            return "success";
        }
        return "doing";
    }

    private String flushOne(StockBasic basic) {

        XQDetail detail = xqProxy.getDetail(basic.getName());
        if (detail == null) {
            return "query one err";
        }
        // ttm, yield, nameCHN, price, h52, l52, liangbi, shizhi, huanshou,
        basic.setPe(detail.getPe_ttm());
        basic.setChn(detail.getName());
        basic.setYield(detail.getDividend_yield());
        basic.setPrice(detail.getCurrent());
        basic.setH52(detail.getHigh52w());
        basic.setL52(detail.getLow52w());
        basic.setLiangbi(detail.getVolume_ratio());
        basic.setShizhi(detail.getMarket_capital() == null ? null : detail.getMarket_capital() / 100000000F);
        basic.setHuanshoulv(detail.getTurnover_rate());
        if (basic.getH52() - basic.getL52() == 0) {
            basic.setHl(1F);
        } else {
            float hl = (basic.getPrice() - basic.getL52()) / (basic.getH52() - basic.getL52());
            basic.setHl((float) (Math.round(hl * 10000) / 10000.0));
        }
        List<XQKline> klineD = xqProxy.getKline(basic.getName(), "day", 69);
        double vtot = 0;
        if (!klineD.isEmpty()) {
            long ts = klineD.get(klineD.size() - 1).getTimestamp();
            if (System.currentTimeMillis() - ts > 86400 * 30 * 1000L && ts > 0) {
                basicRepository.deleteById(basic.getId());
                log.info("delete one stock:{}", basic.getName());
                return null;
            }
            for (XQKline xqKline : klineD) {
                vtot += xqKline.getVolume();
            }
            double vavg = vtot / (double) klineD.size();
            float vcurrent = klineD.get(klineD.size() - 1).getVolume();
            basic.setCjlrateday((float) (vcurrent / vavg));
        }
        Float zcd = ZhiChengUtil.getZhiCheng(klineD, 0.009F);
        basic.setZcrate(zcd);
        List<XQKline> klineW = xqProxy.getKline(basic.getName(), "week", 159);
        Float zcw = ZhiChengUtil.getZhiCheng(klineW, 0.009F);
        basic.setZcweek(zcw);
        Float pef = wbProxy.getPEF(basic.getName(), basic.getMic());
        if (pef != null) {
            basic.setPef(pef);
        } else {
            basic.setPef(1000F);
        }
        basic.setPeg(basic.getPe() / basic.getPef());
        basic.setUp(LocalDate.now().toString());
        basicRepository.save(basic);
        return null;
    }

}
