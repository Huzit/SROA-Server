package com.project.sroa.repository;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    @Query("SELECT s FROM Schedule s WHERE s.userInfo=?1 AND s.status=?2")
    List<Schedule> findAllByEngineerInfoAndStatus(EngineerInfo engineerInfo, Integer status);

    @Modifying
    @Query("UPDATE Schedule s SET s.status=?2 WHERE s.scheduleNum=?1")
    void updateStatus(long scheduleNum, Integer status);

    Schedule findByScheduleNum(Long scheduleNum);
}
