package com.project.sroa.controller;

import com.project.sroa.model.Evaluation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller

public class EvaluationController {
    //애플리케이션과의 통신은 요청이 들어오고 응답을 보내면 연결이 끊기기 떄문에
    //return data를 하면 data를 어플리케이션에 보냄
    @PostMapping("/user/evaluation/writeEvaluation")
    public void writeEvaluation(){

    }
}
