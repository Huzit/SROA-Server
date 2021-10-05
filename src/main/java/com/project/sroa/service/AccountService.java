package com.project.sroa.service;

import com.project.sroa.dto.SignupEngineer;
import com.project.sroa.model.UserInfo;
import org.springframework.stereotype.Service;


public interface AccountService {
    boolean createNewUser(UserInfo userInfo);
    boolean login(String Id, String PW);
    boolean createNewEngineer(SignupEngineer userInfo);
}
