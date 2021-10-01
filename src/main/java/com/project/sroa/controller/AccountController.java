package com.project.sroa.controller;

import com.project.sroa.model.UserInfo;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {
    @Autowired
    UserInfoRepository userR;
    @Autowired
    EngineerInfoRepository engineerR;

    @PostMapping("/user/account/singup")
    public void userSignup(@RequestBody UserInfo userInfo){

        userR.save(userInfo);
    }

    @GetMapping("/user/account/login/{id}/{pw}")
    public void login(){

    }


}
