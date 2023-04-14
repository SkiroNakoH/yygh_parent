package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface HospitalService {

    Hospital getByHoscode(String hosCode);

    void saveHospital(Hospital hospital);
}

