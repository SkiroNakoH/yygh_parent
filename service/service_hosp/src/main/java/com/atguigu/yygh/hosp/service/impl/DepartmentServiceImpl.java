package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 科室服务
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

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
    //TODO 使用mongoTemplate优化性能，只执行一次mongo语句
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);

        if (department != null && department.getIsDeleted() == 0) {
            //逻辑删除
            department.setIsDeleted(1);
            departmentRepository.save(department);
        }
    }

}
