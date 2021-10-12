package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeInfoRepository extends JpaRepository<EmployeeInfo, Long> {

    EmployeeInfo findByEmpNum(Long empNum);
}
