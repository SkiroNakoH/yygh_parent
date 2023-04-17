package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import com.atguigu.yygh.cmn.client.CmnFeignClient;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CmnFeignClient cmnFeignClient;

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

    //分页查询
    @Override
    public Map<String, Object> findPage(Integer page, Integer size, HospitalQueryVo hospitalQueryVo) {
        Map<String, Object> map = new HashMap<>();

        //等值判断
        String provinceCode = hospitalQueryVo.getProvinceCode();
        String cityCode = hospitalQueryVo.getCityCode();

        //模糊匹配
        String hosname = hospitalQueryVo.getHosname();

        //非空判断
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(provinceCode)) {
            //省的编号
            criteria.and("provinceCode").is(provinceCode);
        }
        if (!StringUtils.isEmpty(cityCode)) {
            //市的编号
            criteria.and("cityCode").is(cityCode);
        }
        if (!StringUtils.isEmpty(hosname)) {
            //医院名称, 模拟查询》正则表达式
            criteria.and("hosname").regex(hosname);
        }
        Query query = new Query(criteria);

        //查询总数
        long total = mongoTemplate.count(query, Hospital.class);
        map.put("total", total);


        List<Hospital> hospitalList = mongoTemplate.find(query.skip((page - 1) * size).limit(size), Hospital.class);

        //TODO: 处理省市区名字，以及医院详细地址
        hospitalList.forEach(this::packageHospital);

        map.put("list", hospitalList);
        return map;
    }

    //封装hospital
    private void packageHospital(Hospital hospital) {
        //获取医院等级
        String hostypeString = cmnFeignClient.getNameByParentCodeAndValue(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());

        //拼接省市区
        String provinceName = cmnFeignClient.getNameByValue(hospital.getProvinceCode());
        String cityName = cmnFeignClient.getNameByValue(hospital.getCityCode());
        String districtName = cmnFeignClient.getNameByValue(hospital.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + hospital.getAddress();

        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress",fullAddress);
    }

    //修改医院状态
    @Override
    public void updateStatus(String id, Integer status) {
        Update update = new Update();
        update.set("status",status);

        mongoTemplate.upsert(new Query(Criteria.where("_id").is(id)), update,Hospital.class);
    }

    @Override
    public Hospital getById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();

        packageHospital(hospital);
        return hospital;
    }
}