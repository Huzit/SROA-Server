package com.project.sroa.service;

import com.project.sroa.model.Evaluation;
import com.project.sroa.model.Schedule;

import java.util.List;


public interface EvaluationService {
    boolean storeEvaluation(Evaluation evaluation);

    Schedule updateSchedule(Schedule schedule);

    Schedule updateChargeEmployeeScore(Long scheduleNum, Integer score);

    List<Evaluation> evaluationOfEngineer(long engineerNum);
}
