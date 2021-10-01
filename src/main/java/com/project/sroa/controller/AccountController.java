package com.project.sroa.controller;

import com.project.sroa.model.User;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {
    @Autowired
    UserRepository userR;
    @Autowired
    EngineerInfoRepository engineerR;

    @PostMapping("/user/account/singup")
    public String userSignup(@ModelAttribute User user){
        userR.save(user);
        return null;
    }
}
