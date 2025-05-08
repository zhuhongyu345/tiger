package com.money.tiger.service;

import com.money.tiger.biz.nsdq.NsdqProxy;
import com.money.tiger.dao.StockBasicRepository;
import com.money.tiger.entity.StockBasic;
import com.money.tiger.entity.http.PageQueryReq;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class BasicService {


    @Resource
    private StockBasicRepository basicRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private NsdqProxy nsdqProxy;

    //todo
    public List<StockBasic> search(PageQueryReq req) {
        Query query = new Query();
        if (!StringUtils.isEmpty(req.getPeLow()) || !StringUtils.isEmpty(req.getPeHigh())) {
            query.addCriteria(Criteria.where("pe")
                    .gte(StringUtils.isEmpty(req.getPeLow()) ? Float.MIN_VALUE : req.getPeLow())
                    .lt(StringUtils.isEmpty(req.getPeHigh()) ? Float.MAX_VALUE : req.getPeHigh()));
        }
        if (!StringUtils.isEmpty(req.getHlHigh()) || !StringUtils.isEmpty(req.getHlLow())) {
            query.addCriteria(Criteria.where("hl")
                    .gte(StringUtils.isEmpty(req.getHlLow()) ? Float.MIN_VALUE : req.getHlLow())
                    .lte(StringUtils.isEmpty(req.getHlHigh()) ? Float.MAX_VALUE : req.getHlHigh()));
        }

        if (!StringUtils.isEmpty(req.getZclHigh()) || !StringUtils.isEmpty(req.getZclLow())) {
            query.addCriteria(Criteria.where("zcrate")
                    .gte(StringUtils.isEmpty(req.getZclLow()) ? Float.MIN_VALUE : req.getZclLow())
                    .lte(StringUtils.isEmpty(req.getZclHigh()) ? Float.MAX_VALUE : req.getZclHigh()));
        }

        if (!StringUtils.isEmpty(req.getYield())) {
            query.addCriteria(Criteria.where("yield").gte(req.getYield()));
        }


        if (req.getType() != null) {
            if (req.getType() == -1) {
                query.addCriteria(Criteria.where("tag").is(1));
            } else {
                query.addCriteria(Criteria.where("type").is(req.getType()));
            }
        }


        if (!StringUtils.isEmpty(req.getName())) {
            query = new Query().addCriteria(Criteria.where("name").is(req.getName().toUpperCase()));
        }
        query.with(Sort.by(Sort.Direction.valueOf(req.getSortType().toUpperCase()), req.getSort()));
        query.skip(req.getSkip() == null ? 0 : req.getSkip()).limit(req.getSize());
        return mongoTemplate.find(query, StockBasic.class);
    }

    public String addOne(String name, Integer type, String mic) {
        StockBasic byName = basicRepository.findByName(name);
        if (byName != null || type == null || StringUtils.isEmpty(name)) {
            return "fail";
        }
        if (StringUtils.isEmpty(mic)) {
            mic = nsdqProxy.getOne(name).getMicCode();
        }
        basicRepository.save(new StockBasic().setName(name.toUpperCase()).setType(type).setPe(0F).setMic(mic));
        return "success";
    }

    public String tagOne(String id, Integer tag) {
        Optional<StockBasic> byId = basicRepository.findById(id);
        if (byId.isPresent()) {
            StockBasic stockBasic = byId.get();
            basicRepository.save(stockBasic.setTag(tag));
            return "success";
        }
        return "missing";
    }

    public String deleteOne(String id) {
        basicRepository.deleteById(id);
        return "success";
    }
}
