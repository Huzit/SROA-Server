package com.project.sroa.service;

import com.project.sroa.dto.WriteEvaluation;
import com.project.sroa.model.Evaluation;

import java.util.List;


public interface EvaluationService {
    boolean storeEvaluation(WriteEvaluation form);

    List<Evaluation> evaluationOfEngineer(long engineerNum);
}
