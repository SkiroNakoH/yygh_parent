package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.cmn.client.CmnFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-21
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private CmnFeignClient cmnFeignClient;

    @Override
    public List<Patient> findByUserId(Long userId) {

        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);

        List<Patient> patientList = baseMapper.selectList(queryWrapper);

        patientList.forEach(this::packagePatient);

        return patientList;
    }

    @Override
    public Patient getById(Serializable id) {
        Patient patient = super.getById(id);
        packagePatient(patient);
        return patient;
    }

    private void packagePatient(Patient patient) {
        //证据类型
        String certificatesTypeString = cmnFeignClient.getNameByParentCodeAndValue(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());

        //省
        String provinceString = cmnFeignClient.getNameByValue(patient.getProvinceCode());
        //市
        String cityString = cmnFeignClient.getNameByValue(patient.getCityCode());
        //区域
        String districtString = cmnFeignClient.getNameByValue(patient.getDistrictCode());


        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        //具体详细地址->    省+市+区+详细地址
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());

    }
}
