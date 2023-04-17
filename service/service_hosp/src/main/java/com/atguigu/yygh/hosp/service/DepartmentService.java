package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentService {
    void save(Department department);

    Page<Department> findPage(String hosCode, Integer page, Integer pageSize);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDepartmentTree(String hoscode);
}
