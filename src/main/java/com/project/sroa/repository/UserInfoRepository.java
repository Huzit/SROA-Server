package com.project.sroa.repository;

import com.project.sroa.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    List<UserInfo> findAll();
}
