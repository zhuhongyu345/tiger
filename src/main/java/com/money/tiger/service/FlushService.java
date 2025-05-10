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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        try {
            if (basic.getName().contains("-") || basic.getName().contains(".")) {
                basicRepository.deleteById(basic.getId());
                return null;
            }

            XQDetail detail = xqProxy.getDetail(basic.getName());
            if (detail == null) {
                return "query one err";
            }
            // ttm, yield, nameCHN, price, h52, l52, liangbi, shizhi, huanshou,
            basic.setPe(detail.getPe_ttm());
            basic.setChn(detail.getName());
            basic.setYield(detail.getDividend_yield().setScale(2, RoundingMode.HALF_UP));
            basic.setPrice(detail.getCurrent());
            basic.setH52(detail.getHigh52w());
            basic.setL52(detail.getLow52w());
            basic.setLiangbi(detail.getVolume_ratio());
            basic.setShizhi(detail.getMarket_capital() == null ? null : detail.getMarket_capital().divide(new BigDecimal("100000000"), 4, RoundingMode.HALF_UP));
            basic.setHuanshoulv(detail.getTurnover_rate());
            if (basic.getH52().subtract(basic.getL52()).floatValue() == 0F) {
                basic.setHl(new BigDecimal("1"));
            } else {
                BigDecimal hl = basic.getPrice().subtract(basic.getL52()).divide(basic.getH52().subtract(basic.getL52()), 4, RoundingMode.HALF_UP);
                basic.setHl(hl);
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
                    vtot += xqKline.getVolume().doubleValue();
                }
                double vavg = vtot / (double) klineD.size();
                float vcurrent = klineD.get(klineD.size() - 1).getVolume();
                basic.setCjlrateday(new BigDecimal(vcurrent / vavg).setScale(4, RoundingMode.HALF_UP));
            }
            Float zcd = ZhiChengUtil.getZhiCheng(klineD, 0.009F);
            basic.setZcrate(new BigDecimal(zcd).setScale(4, RoundingMode.HALF_UP));
            List<XQKline> klineW = xqProxy.getKline(basic.getName(), "week", 159);
            Float zcw = ZhiChengUtil.getZhiCheng(klineW, 0.009F);
            basic.setZcweek(new BigDecimal(zcw).setScale(4, RoundingMode.HALF_UP));
            BigDecimal pef = wbProxy.getPEF(basic.getName(), basic.getMic());
            if (pef != null) {
                basic.setPef(pef.setScale(4, RoundingMode.HALF_UP));
            } else {
                basic.setPef(new BigDecimal("1000"));
            }
            basic.setPeg(basic.getPe() == null ? new BigDecimal("0") : basic.getPe().divide(basic.getPef(), 4, RoundingMode.HALF_UP));
            basic.setUp(LocalDate.now().toString());
            basicRepository.save(basic);
            return null;
        } catch (Exception e) {
            log.error("", e);
            return e.getMessage();
        }
    }

}
