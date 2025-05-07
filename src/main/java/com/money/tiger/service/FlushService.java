package com.money.tiger.service;

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

    public String flush(Integer hard, Integer type) {
        if (!doing || type == -1) {
            doing = true;
            new Thread(() -> {
                Criteria criteria = new Criteria();
                if (type != null) {
                    if (type > 0) {
                        criteria.and("tag").is(1);
                    } else {
                        criteria.and("type").is(type);
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

    private String flushOne(StockBasic stockBasic) {
        return null;
    }

}
