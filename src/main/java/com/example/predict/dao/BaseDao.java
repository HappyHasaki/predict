package com.example.predict.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BaseDao
 * @Author zlm
 * @Date 2021/3/30 1:24
 */
@Repository
public class BaseDao<T> {
    @PersistenceContext
    public EntityManager entityManager;
    private Class<T> entityClass;

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    public String getEntityName() {
        return getEntityClass().getName();
    }

    public T createEntity() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void save(T entity) {
        Session session = currentSession();
        session.save(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public T getById(int id) {
        Session session = currentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getEntityClass());
        Root<T> root = criteriaQuery.from(getEntityClass());
        criteriaQuery
                .select(root)
                .where(
                        builder.equal(root.get("id"), id)
                );
        Query<T> query = session.createQuery(criteriaQuery);
        return query.uniqueResult();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public T getByIdContainsDeleted(int id) {
        Session session = currentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getEntityClass());
        Root<T> root = criteriaQuery.from(getEntityClass());
        criteriaQuery
                .select(root)
                .where(
                        builder.equal(root.get("id"), id)
                );
        Query<T> query = session.createQuery(criteriaQuery);
        return query.uniqueResult();
    }


    //给字段带isDeleted的实体用
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> getByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        Session session = currentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getEntityClass());
        Root<T> root = criteriaQuery.from(getEntityClass());
        criteriaQuery
                .select(root)
                .where(
                        root.get("id").in(ids)
                );
        Query<T> query = session.createQuery(criteriaQuery);
        return query.list();
    }

    @Transactional
    public T getByIdForUpdate(long id) {
        Session session = currentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getEntityClass());
        Root<T> root = criteriaQuery.from(getEntityClass());
        criteriaQuery
                .select(root)
                .where(
                        builder.equal(root.get("id"), id)
                );
        Query<T> query = session.createQuery(criteriaQuery).setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.uniqueResult();
    }

    @Transactional
    public void update(T entity) {
        Session session = currentSession();
        session.update(entity);
    }


    @Transactional
    public void saveOrUpdate(T entity) {
        Session session = currentSession();
        session.saveOrUpdate(entity);
    }

    @Transactional
    public void updates(List<T> entities) {
        if (entities.isEmpty()) {
            return;
        }
        Session session = currentSession();
        AbstractEntityPersister entityPersister = getEntityPersister();

        String tableName = entityPersister.getTableName(); // question_answer_mark
        String identifierPropertyName = entityPersister.getIdentifierPropertyName(); // id
        String[] identifierColumnNames = entityPersister.getIdentifierColumnNames(); // [id]

        String[] propertyNames = entityPersister.getPropertyNames(); // [title, createTime, status, ...] 没有id

        // 设置SQL语句
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            session.evict(entity);
            sql.append(" update ").append(tableName);

            // set
            sql.append(" set ");
            List<String> sets = new ArrayList<>();
            for (String propertyName : propertyNames) {
                for (String propertyColumnName : entityPersister.getPropertyColumnNames(propertyName)) {
                    sets.add(propertyColumnName + " = :" + propertyName + i);
                }
            }
            sql.append(String.join(", ", sets));

            // where
            sql.append(" where ");
            List<String> wheres = new ArrayList<>();
            for (String identifierColumnName : identifierColumnNames) {
                wheres.add(identifierColumnName + " = :" + identifierPropertyName + i);
            }
            sql.append(String.join("and ", wheres));

            sql.append(" ;");
        }

//        System.out.println(sql);

        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);

            for (String propertyName : propertyNames) {
                sqlQuery.setParameter(propertyName + i, entityPersister.getPropertyValue(entity, propertyName));
            }
            sqlQuery.setParameter(identifierPropertyName + i, entityPersister.getIdentifier(entity));
        }
        sqlQuery.executeUpdate();
    }

    @Transactional
    public void saves(List<T> entities) {
        if (entities.isEmpty()) {
            return;
        }
        Session session = currentSession();
        AbstractEntityPersister entityPersister = getEntityPersister();

        String tableName = entityPersister.getTableName(); // question_answer_mark

        String[] propertyNames = entityPersister.getPropertyNames(); // [title, createTime, status, ...] 没有id

        // 设置SQL语句
        String sql = "insert into " + tableName + "(";

        // column names and value property names
        List<String> columnNames = new ArrayList<>(); // create_time
        List<String> valuePropertyNames = new ArrayList<>(); // createTime
//        List<String> valueParamNames = new ArrayList<>(); // :createTime
        for (String propertyName : propertyNames) {
            for (String columnName : entityPersister.getPropertyColumnNames(propertyName)) {
                columnNames.add(columnName);
                valuePropertyNames.add(propertyName);
//                valueParamNames.add(":" + propertyName);
            }
        }

        sql += String.join(", ", columnNames);
        sql += ")";
        sql += " values ";
        List<String> values = new ArrayList<>();
        for (int i = 0; i < entities.size(); ++i) {
            T entity = entities.get(i);
            int finalI = i;
            List<String> paramNames = valuePropertyNames.stream()
                    .map(o -> ":" + o + finalI)
                    .collect(Collectors.toList()); // :createTime0
            values.add("(" + String.join(", ", paramNames) + ")");
        }
        sql += String.join(", ", values);
        sql += ";";

//        System.out.println(sql);

        // set params
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);

            for (String propertyName : propertyNames) {
                sqlQuery.setParameter(propertyName + i, entityPersister.getPropertyValue(entity, propertyName));
            }
        }
        sqlQuery.executeUpdate();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void selects() {
    }

    /**
     * 目前只支持identifier为Number子类型的entity
     *
     * @param entities
     */
    public void saveOrUpdateList(List<T> entities) {
        AbstractEntityPersister entityPersister = getEntityPersister();

        List<T> saveEntities = entities.stream()
                .filter(o -> ((Number) entityPersister.getIdentifier(o)).longValue() == 0)
                .collect(Collectors.toList());

        List<T> updateEntities = entities.stream()
                .filter(o -> ((Number) entityPersister.getIdentifier(o)).longValue() > 0)
                .collect(Collectors.toList());

        if (!saveEntities.isEmpty()) {
            saves(saveEntities);
        }
        if (!updateEntities.isEmpty()) {
            updates(updateEntities);
        }
    }

    public void evict(T entity) {
        currentSession().evict(entity);
    }

    private AbstractEntityPersister getEntityPersister() {
        Session session = currentSession();
        SessionFactory sessionFactory = session.getSessionFactory();
        MetamodelImplementor metamodel = (MetamodelImplementor) sessionFactory.getMetamodel();
        return (AbstractEntityPersister) metamodel.entityPersister(getEntityClass());
    }
}
