package com.money.tiger.service;

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

    //todo
    public List<StockBasic> search(PageQueryReq req) {
        Query query = new Query();
        if (!StringUtils.isEmpty(req.getPeLow()) || !StringUtils.isEmpty(req.getPeHigh())) {
            query.addCriteria(Criteria.where("pe")
                    .gt(StringUtils.isEmpty(req.getPeLow()) ? Float.MIN_VALUE : req.getPeLow())
                    .lt(StringUtils.isEmpty(req.getPeHigh()) ? Float.MAX_VALUE : req.getPeHigh()));
        }
        if (!StringUtils.isEmpty(req.getHlHigh()) || !StringUtils.isEmpty(req.getHlLow())) {
            query.addCriteria(Criteria.where("hl")
                    .gte(StringUtils.isEmpty(req.getHlLow()) ? Float.MIN_VALUE : req.getHlLow())
                    .lte(StringUtils.isEmpty(req.getHlHigh()) ? Float.MAX_VALUE : req.getHlHigh()));
        }

        if (!StringUtils.isEmpty(req.getYield())) {
            query.addCriteria(Criteria.where("yield").gte(req.getYield()));
        }


        if (!StringUtils.isEmpty(req.getName())) {
            query.addCriteria(Criteria.where("name").is(req.getName()));
        }
        query.with(Sort.by(Sort.Direction.valueOf(req.getSortType().toUpperCase()),req.getSort()));
        query.skip(req.getSkip()).limit(req.getSize());
        return mongoTemplate.find(query, StockBasic.class);
    }

    public void addOne(String name, Integer type) {
        StockBasic byName = basicRepository.findByName(name);
        if (byName != null || type == null) {
            return;
        }
        basicRepository.save(new StockBasic().setName(name).setType(type).setTag(1));
    }

    public void tagOne(String id, Integer tag) {
        Optional<StockBasic> byId = basicRepository.findById(id);
        if (byId.isPresent()) {
            StockBasic stockBasic = byId.get();
            basicRepository.save(stockBasic.setTag(tag));
        }
    }

    public void deleteOne(String id) {
        basicRepository.deleteById(id);
    }
}
