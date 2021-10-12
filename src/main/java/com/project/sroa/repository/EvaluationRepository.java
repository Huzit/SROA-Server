package com.project.sroa.repository;

import com.project.sroa.model.Evaluation;
import com.project.sroa.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Evaluation findBySchedule(Schedule schedule);

}
