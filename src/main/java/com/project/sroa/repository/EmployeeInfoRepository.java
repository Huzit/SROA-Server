package com.project.sroa.repository;

import com.project.sroa.model.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeInfoRepository extends JpaRepository<EmployeeInfo,Long> {

}
