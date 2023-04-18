package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 科室服务
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    //新增或保存科室
    @Override
    public void save(Department department) {
        //根据depcode判断department是否存在
        Department departmentDB = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (departmentDB == null) {
            //新增
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
        } else {
            //修改
            department.setId(departmentDB.getId());
            department.setCreateTime(departmentDB.getCreateTime());
            department.setIsDeleted(departmentDB.getIsDeleted());
        }

        department.setUpdateTime(new Date());
        departmentRepository.save(department);
    }

    //分页查询科室
    @Override
    public Page<Department> findPage(String hosCode, Integer page, Integer pageSize) {
        Department department = new Department();
        department.setHoscode(hosCode);
        //逻辑未删除
        department.setIsDeleted(0);

        return departmentRepository.findAll(Example.of(department), PageRequest.of(page - 1, pageSize));
    }

    //根据hoscode和depcode删除科室
    @Override
    public void remove(String hoscode, String depcode) {
      /*  Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);

        if (department != null && department.getIsDeleted() == 0) {
            //逻辑删除
            department.setIsDeleted(1);
            departmentRepository.save(department);
        }*/
        Query query = new Query(Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode));

        Update update = new Update();
        update.set("isDeleted", 1);
        mongoTemplate.upsert(query, update, Department.class);
    }

    @Override
    public List<DepartmentVo> findDepartmentTree(String hoscode) {
        List<Department> departmentList = departmentRepository.findByHoscode(hoscode);

        ArrayList<DepartmentVo> finalList = new ArrayList<>();
        //处理数据  --->    按bigname科室名分组
        Map<String, List<Department>> map = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for (Map.Entry<String, List<Department>> entry : map.entrySet()) {
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepname(entry.getValue().get(0).getBigname());     //设置科室名
            departmentVo.setDepcode(entry.getValue().get(0).getBigcode());     //设置科室编号

            //处理子数据
            ArrayList<DepartmentVo> childrenList = new ArrayList<>();
            for (Department department : entry.getValue()) {
                DepartmentVo childrenVo = new DepartmentVo();
                childrenVo.setDepname(department.getDepname());
                childrenVo.setDepcode(department.getDepcode());
                childrenList.add(childrenVo);
            }

            //添加子数据
            departmentVo.setChildren(childrenList);

            finalList.add(departmentVo);
        }


        return finalList;
    }

}
