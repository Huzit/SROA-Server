package com.project.sroa.controller;

import com.project.sroa.dto.SignupEngineer;
import com.project.sroa.model.UserInfo;

import com.project.sroa.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class AccountController {
    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    // 고객 회원가입
    @PostMapping("/account/customer/singup")
    public boolean userSignup(@RequestBody UserInfo userInfo){
        return  accountService.createNewUser(userInfo);
    }

    // 엔지니어 회원가입
    @PostMapping("/account/engineer/singup")
    public boolean engineerSignup(@RequestBody SignupEngineer info){
        return accountService.createNewEngineer(info);
    }

    // 고객, 엔지니어 로그인
    @GetMapping("/account/login/{ID}/{PW}")
    public boolean login(@PathVariable("ID") String id, @PathVariable("PW") String pw){

        return accountService.login(id, pw);
    }
}
