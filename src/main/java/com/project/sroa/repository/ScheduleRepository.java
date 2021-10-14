package com.project.sroa.repository;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.engineerInfo=?1 AND s.status=?2")
    List<Schedule> findAllByEngineerInfoAndStatus(EngineerInfo engineerInfo, Integer status);


//    @Query(nativeQuery = true, value="SELECT s.* FROM Schedule s WHERE s.engineer_num=?1 AND s.status=?2 AND s.start_date like concat('%', ?3, '%')")
//    List<Schedule> findAllByEngineerInfoAndStatusAndStartDateContains(Long e, Integer status, String date);


    // 엔지니어의 날짜의 일정
    @Query(nativeQuery = true, value = "SELECT s.* FROM schedule s WHERE s.start_date like concat('%', ?2, '%') AND s.engineer_num= (SELECT e.engineer_num FROM engineer_info e WHERE e.engineer_num=?1) ORDER BY s.start_date ASC")
    List<Schedule> findAllScheduleTimeByEngineerNumAndDate(Long engineerNum, String date);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.status=?2 WHERE s.scheduleNum=?1")
    void updateStatus(long scheduleNum, Integer status);

    Schedule findByScheduleNum(Long scheduleNum);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.endDate=?2 WHERE s.scheduleNum=?1")
    void updateEndDate(Long scheduleNum, Timestamp valueOf);

    @Query(nativeQuery = true, value = "SELECT s.* FROM Schedule s WHERE s.start_date like concat('%', ?2, '%') AND s.engineer_num=?1")
    List<Schedule> findAllByEngineerInfoAndDateTime(Long engineerNum, String dateTime);
}
