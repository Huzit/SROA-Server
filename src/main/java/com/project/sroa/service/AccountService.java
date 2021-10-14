package com.project.sroa.service;

import com.project.sroa.model.UserInfo;


public interface AccountService {
    boolean createNewUser(UserInfo userInfo);

    boolean login(String Id, String PW);

    boolean createNewEngineer(UserInfo userInfo, String centerName, Long empNum);

    boolean checkDuplicateEmp(Long empNum);

    boolean checkDuplicateId(String Id);
}
