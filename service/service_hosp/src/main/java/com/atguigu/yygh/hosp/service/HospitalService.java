package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface HospitalService {

    Hospital getByHoscode(String hosCode);

    void saveHospital(Hospital hospital);

    Map<String, Object> findPage(Integer page, Integer size, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);
}

