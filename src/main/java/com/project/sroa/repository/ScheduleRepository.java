package com.project.sroa.repository;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    @Query("SELECT s FROM Schedule s WHERE s.engineerInfo=?1 AND s.status=?2")
    List<Schedule> findAllByEngineerInfoAndStatus(EngineerInfo engineerInfo, Integer status);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.status=?2 WHERE s.scheduleNum=?1")
    void updateStatus(long scheduleNum, Integer status);

    Schedule findByScheduleNum(Long scheduleNum);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.endDate=?2 WHERE s.scheduleNum=?1")
    void updateEndDate(Long scheduleNum ,Timestamp valueOf);
}
