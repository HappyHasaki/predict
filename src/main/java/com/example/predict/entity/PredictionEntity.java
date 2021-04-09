package com.example.predict.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @ClassName PredictionEntity
 * @Author zlm
 * @Date 2021/4/10 1:16
 */
@Entity
@Table(name = "prediction", schema = "predict", catalog = "")
public class PredictionEntity {
    private int id;
    private Integer code;
    private Double predictPrice;
    private Double realPrice;
    private Boolean isCorrect;
    private Timestamp createTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "code")
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Basic
    @Column(name = "predict_price")
    public Double getPredictPrice() {
        return predictPrice;
    }

    public void setPredictPrice(Double predictPrice) {
        this.predictPrice = predictPrice;
    }

    @Basic
    @Column(name = "real_price")
    public Double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(Double realPrice) {
        this.realPrice = realPrice;
    }

    @Basic
    @Column(name = "is_correct")
    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredictionEntity that = (PredictionEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(predictPrice, that.predictPrice) &&
                Objects.equals(realPrice, that.realPrice) &&
                Objects.equals(isCorrect, that.isCorrect) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, predictPrice, realPrice, isCorrect, createTime);
    }
}
