package com.example.predict.dao;

import com.example.predict.entity.PredictionEntity;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName PredictionDao
 * @Author zlm
 * @Date 2021/4/10 1:16
 */
public class PredictionDao extends BaseDao<PredictionEntity> {
    public PredictionDao() {
        setEntityClass(PredictionEntity.class);
    }

    @Override
    public PredictionEntity createEntity() {
        PredictionEntity predictionEntity = new PredictionEntity();
        predictionEntity.setId(0);
        predictionEntity.setPredictPrice(0.0);
        predictionEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return predictionEntity;
    }

    public List<PredictionEntity> getByCode(int code) {
        Query<PredictionEntity> query = currentSession().createQuery(
                "from PredictionEntity where code =: code", PredictionEntity.class)
                .setParameter("code", code);
        return query.list();
    }
}
