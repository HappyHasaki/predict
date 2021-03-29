package com.example.predict.dao;

import com.example.predict.entity.ClosingEntity;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * @ClassName ClosingDao
 * @Author zlm
 * @Date 2021/3/30 1:34
 */
@Repository
public class ClosingDao extends BaseDao<ClosingEntity> {
    public ClosingDao() {
        setEntityClass(ClosingEntity.class);
    }

    @Override
    public ClosingEntity createEntity() {
        ClosingEntity closingEntity = new ClosingEntity();
        closingEntity.setId(0);
        closingEntity.setPrice(0);
        closingEntity.setTime(new Timestamp(System.currentTimeMillis()));
        return closingEntity;
    }

    public List<ClosingEntity> getAll() {
        Query<ClosingEntity> query = currentSession().createQuery(
                "from ClosingEntity where id < 7320",
                ClosingEntity.class
        );
        return query.list();
    }
}
