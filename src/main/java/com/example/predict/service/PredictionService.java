package com.example.predict.service;

import com.example.predict.dao.PredictionDao;
import com.example.predict.entity.PredictionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName PredictionService
 * @Author zlm
 * @Date 2021/4/12 18:04
 */
@Service
public class PredictionService {
    @Autowired
    private PredictionDao predictionDao;

    public List<PredictionEntity> getByCode(int code) {
        return predictionDao.getByCode(code);
    }
}
