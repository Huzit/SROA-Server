package com.project.sroa.controller;

import com.project.sroa.dto.WriteEvaluation;
import com.project.sroa.model.Evaluation;
import com.project.sroa.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller

public class EvaluationController {
    //애플리케이션과의 통신은 요청이 들어오고 응답을 보내면 연결이 끊기기 떄문에
    //return data를 하면 data를 어플리케이션에 보냄
    EvaluationService evaluationService;
    @Autowired
    public EvaluationController(EvaluationService evaluationService){
        this.evaluationService=evaluationService;
    }

    @PostMapping("/evaluation/customer/writeEvaluation")
    public boolean writeEvaluation(@RequestBody WriteEvaluation form){
        return evaluationService.storeEvaluation(form);
    }

    @GetMapping("/evaluation/engineer/askEvaluation/{engineerNum}")
    public List<Evaluation> askEvaluation(@RequestParam("engineerNum") long engineerNum){
        return evaluationService.evaluationOfEngineer(engineerNum);
    }
}
