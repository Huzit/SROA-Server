package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import com.project.sroa.model.EngineerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface EngineerInfoRepository extends JpaRepository<EngineerInfo,Long> {

    EngineerInfo findByEmployeeInfo(EmployeeInfo employeeInfo);

    @Transactional
    @Modifying
    @Query("UPDATE EngineerInfo e SET e.avgScore=?2 WHERE e.engineerNum=?1")
    void updateEngineerScore(long engineerNum, Integer avgScore);

    @Transactional
    @Modifying
    @Query("UPDATE EngineerInfo e SET e.amountOfWork=e.amountOfWork+1 WHERE e.engineerNum=?1")
    void updateEngineerAmountOfWork(long engineerNum);

    EngineerInfo findByEngineerNum(long engineerNum);
}
