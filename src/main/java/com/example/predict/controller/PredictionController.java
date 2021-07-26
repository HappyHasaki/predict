package com.example.predict.controller;

import com.example.predict.entity.PredictionEntity;
import com.example.predict.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName PredictionController
 * @Author zlm
 * @Date 2021/4/12 18:04
 */
@RestController
@RequestMapping("/api")
public class PredictionController {
    @Autowired
    private PredictionService predictionService;

    @RequestMapping(value = "/prediction", method = RequestMethod.GET)
    public List<PredictionEntity> getQuestionDetailBatch(@RequestParam int code) {
        return predictionService.getByCode(code);
    }
}
