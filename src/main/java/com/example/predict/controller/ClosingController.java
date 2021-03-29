package com.example.predict.controller;

import com.example.predict.entity.ClosingEntity;
import com.example.predict.service.ClosingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ClosingController
 * @Author zlm
 * @Date 2021/3/30 1:29
 */
@RestController
@RequestMapping("/api/internal")
public class ClosingController {
    @Autowired
    private ClosingService closingService;

    @RequestMapping(value = "/closing/list", method = RequestMethod.GET)
    public List<ClosingEntity> getQuestionDetailBatch() {
        return closingService.getAll();
    }
}
