package com.project.sroa.service;

import com.project.sroa.dto.SignupEngineer;
import com.project.sroa.model.EmployeeInfo;
import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.ServiceCenter;
import com.project.sroa.model.UserInfo;
import com.project.sroa.repository.EmployeeInfoRepository;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.ServiceCenterRepository;
import com.project.sroa.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService{
    UserInfoRepository userInfoRepository;
    EngineerInfoRepository engineerInfoRepository;
    EmployeeInfoRepository employeeInfoRepository;
    ServiceCenterRepository serviceCenterRepository;
    @Autowired
    public AccountServiceImpl(UserInfoRepository userInfoRepository,
                              EngineerInfoRepository engineerInfoRepository,
                              EmployeeInfoRepository employeeInfoRepository,
                              ServiceCenterRepository serviceCenterRepository){
        this.userInfoRepository=userInfoRepository;
        this.engineerInfoRepository=engineerInfoRepository;
        this.employeeInfoRepository=employeeInfoRepository;
        this.serviceCenterRepository=serviceCenterRepository;
    }


    @Override
    //고객의 회원가입
    public boolean createNewUser(UserInfo userInfo){
        if(userInfoRepository.existsById(userInfo.getId())){
            System.out.println("고객 회원가입 : 이미 존재하는 아이디");
            return false;
        }
        userInfo.setCode(1);
        userInfoRepository.save(userInfo);
        System.out.println("고객 회원가입 : 회원가입 성공");
        return true;
    }

    @Override
    // 엔지니어의 회원가입
    public boolean createNewEngineer(SignupEngineer info) {
        EmployeeInfo employeeInfo=employeeInfoRepository.findByEmpNum(info.getEmpNum());
        //사용가능한 사원번호인지
        if(employeeInfo==null){
            System.out.println("엔지니어 회원가입 : 가입되지 않은 사원번호입니다.");
            return false;
        }


        //해당 사원번호로 가입된 엔지니어가 있는지
        if(engineerInfoRepository.findByEmployeeInfo(employeeInfo) != null){
            System.out.println("엔지니어 회원가입 : 이미 가입된 사원번호입니다.");
            return false;
        }

        // 아이디가 이미 존재
        if(userInfoRepository.existsById(info.getId())){
            System.out.println("엔지니어 회원가입 : 이미 존재하는 아이디");
            return false;
        }

        UserInfo userInfo=UserInfo.builder()
                .id(info.getId())
                .pw(info.getPw())
                .address(info.getAddress())
                .name(info.getName())
                .phoneNum(info.getPhoneNum())
                .build();
        userInfo.setCode(2);
        userInfoRepository.save(userInfo);

        ServiceCenter serviceCenter=serviceCenterRepository.findByCenterName(info.getCenterName());

        EngineerInfo engineerInfo=EngineerInfo.builder()
                .employeeInfo(employeeInfo)
                .serviceCenter(serviceCenter)
                .userInfo(userInfo)
                .build();

        engineerInfoRepository.save(engineerInfo);
        System.out.println("엔지니어 회원가입 : 성공");

        return true;
    }

    @Override
    //고객 로그인
    public boolean login(String Id, String PW) {
        UserInfo userInfo=userInfoRepository.findById(Id);
        if(userInfo==null){
            System.out.println("로그인 : 존재하지않는 아이디");
            return false;
        }

        if(PW.equals(userInfo.getPw())){
            System.out.println("로그인 : 성공");
            return true;
        }
        System.out.println("로그인 : 비밀번호가 틀림");
        return false;
    }
}
