package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    //新增或修改医院信息
    @Override
    public void saveHospital(Hospital hospital) {
        //处理http传参，“+”转为" "问题
        String logoData = hospital.getLogoData().replaceAll(" ", "+");
        hospital.setLogoData(logoData);

        //根据hoscode查找hospital
        Hospital hospitalDB = hospitalRepository.findByHoscode(hospital.getHoscode());
        //判断是否存在
        if (null != hospitalDB) {
            //更新医院数据
            hospital.setId(hospitalDB.getId()); //根据id更新
            hospital.setStatus(hospitalDB.getStatus());
            hospital.setCreateTime(hospitalDB.getCreateTime());
            hospital.setIsDeleted(hospitalDB.getIsDeleted());
        } else {
            //新增医院
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setIsDeleted(0);
        }
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    //根据hoscode查找hospital
    @Override
    public Hospital getByHoscode(String hosCode) {
        return hospitalRepository.findByHoscode(hosCode);
    }

}