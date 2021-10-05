package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import com.project.sroa.model.EngineerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface EngineerInfoRepository extends JpaRepository<EngineerInfo,Long> {

    EngineerInfo findByEmployeeInfo(EmployeeInfo employeeInfo);

    @Transactional
    @Modifying
    @Query("UPDATE EngineerInfo e SET e.avgScore=?2 WHERE e.engineerNum=?1")
    void updateEngineerScore(long engineerNum, Integer avgScore);

    EngineerInfo findByEngineerNum(long engineerNum);
}
