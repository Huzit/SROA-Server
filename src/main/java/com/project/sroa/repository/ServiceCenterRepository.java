package com.project.sroa.repository;

import com.project.sroa.model.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long> {
    ServiceCenter findByCenterName(String centerName);
    List<ServiceCenter> findByAddressContaining(String rootAddress);
}
