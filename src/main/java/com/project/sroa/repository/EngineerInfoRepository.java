package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface EngineerInfoRepository extends JpaRepository<EngineerInfo,Long> {
    EngineerInfo findByEmployeeInfo(EmployeeInfo employeeInfo);
    EngineerInfo findByEngineerNum(long engineerNum);
    List<EngineerInfo> findByServiceCenter(ServiceCenter serviceCenter);

    @Transactional
    @Modifying
    @Query("UPDATE EngineerInfo e SET e.avgScore=?2 WHERE e.engineerNum=?1")
    void updateEngineerScore(long engineerNum, Integer avgScore);

    @Transactional
    @Modifying
    @Query("UPDATE EngineerInfo e SET e.amountOfWork=e.amountOfWork+1 WHERE e.engineerNum=?1")
    void updateEngineerAmountOfWork(long engineerNum);

    //센터의 엔지니어중 해당 시간에 일정이 없는 엔지니어
    @Query(nativeQuery = true, value="SELECT e.* FROM engineer_info e WHERE e.center_num =?1 AND e.engineer_num NOT IN (SELECT s.engineer_num FROM schedule s WHERE s.start_date like concat('%', ?2, '%'))")
    List<EngineerInfo> findAllPossibleEngineerByDate(Long centerNum, String date);




}
