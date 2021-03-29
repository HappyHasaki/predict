package com.example.predict.service;

import com.example.predict.dao.ClosingDao;
import com.example.predict.entity.ClosingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ClosingService
 * @Author zlm
 * @Date 2021/3/30 1:30
 */
@Service
public class ClosingService {
    @Autowired
    private ClosingDao closingDao;

    public List<ClosingEntity> getAll() {
        return closingDao.getAll();
    }
}
