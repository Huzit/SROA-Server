package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import com.project.sroa.model.EngineerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngineerInfoRepository extends JpaRepository<EngineerInfo,Long> {
    List<EngineerInfo> findAll();

//    boolean existsByEmployeeInfo(EmployeeInfo employeeInfo);

    EngineerInfo findByEmployeeInfo(EmployeeInfo employeeInfo);
}
