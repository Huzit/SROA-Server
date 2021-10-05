package com.project.sroa.service;

import com.project.sroa.dto.WriteEvaluation;
import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Evaluation;
import com.project.sroa.model.Schedule;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.EvaluationRepository;
import com.project.sroa.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    ScheduleRepository scheduleRepository;
    EvaluationRepository evaluationRepository;
    EngineerInfoRepository engineerInfoRepository;

    @Autowired
    public EvaluationServiceImpl(ScheduleRepository scheduleRepository,
                                 EvaluationRepository evaluationRepository,
                                 EngineerInfoRepository engineerInfoRepository){
        this.scheduleRepository=scheduleRepository;
        this.evaluationRepository=evaluationRepository;
        this.engineerInfoRepository=engineerInfoRepository;
    }

    @Override
    public boolean storeEvaluation(WriteEvaluation form) {
        // ? 고치는 기사, 가져다 주는 기사가 따로 있으면 평가는 누구? 일단은 수리 신청 받은 기사가 받는 걸로
        Schedule schedule=scheduleRepository.findByScheduleNum(form.getScheduleNum());

        // 기사의 평균 평점 수정
        long totalScore=0;
        List<Schedule> list= scheduleRepository.findAllByEngineerInfoAndStatus(schedule.getEngineerInfo(), 4);

        for(Schedule s:list ){
            totalScore+=evaluationRepository.findBySchedule(s).getScore();
        }

        totalScore+=form.getScore();
        Integer avgScore= Math.toIntExact(totalScore / (list.size()+1));
        engineerInfoRepository.updateEngineerScore(schedule.getEngineerInfo().getEngineerNum(), avgScore);
        System.out.println("평가 작성 : 엔지니어 평균 평점 갱신");

        // 해당 schedule status 평가완료 변경
        scheduleRepository.updateStatus(schedule.getScheduleNum(), 4);
        System.out.println("평가 작성 : 일정 상태 변경(평가작성 완료)");
        // 평가 작성
        Evaluation evaluation = Evaluation.builder()
                .writeDate(form.getWriteDate())
                .content(form.getContent())
                .score(form.getScore())
                .schedule(schedule)
                .build();
        evaluationRepository.save(evaluation);
        System.out.println("평가 작성 : 평가 저장");
        return true;
    }

    @Override
    public List<Evaluation> evaluationOfEngineer(long engineerNum) {
        EngineerInfo engineerInfo = engineerInfoRepository.findByEngineerNum(engineerNum);

        List<Schedule> list=scheduleRepository.findAllByEngineerInfoAndStatus(engineerInfo, 4);
        System.out.println("평가 조회 : 평가 완료된 일정에 대해 해당 엔지니어 일정 조회");

        List<Evaluation> res = new ArrayList<>();
        for(Schedule s:list){
            res.add(evaluationRepository.findBySchedule(s));
        }
        System.out.println("평가 조회 : 평가 완료된 일정에 대해 해당 엔지니어 평가 조회");
        return res;
    }
}
