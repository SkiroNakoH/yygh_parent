package com.atguigu.yygh.cmn.controller;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-11
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("字典列表查询")
    @GetMapping({"/findListByParentId/{parentId}", "/findListByParentId"})
    public Result findListByParentId(@ApiParam("父Id") @PathVariable(required = false) Long parentId) {
        if (parentId == null) {
            parentId = 1L;
        }

        List<Dict> dictList = dictService.findListByParentId(parentId);
        return Result.ok().data("dictList", dictList);
    }

    /**
     * 读取数据库，上传Excel,让前端下载Excel
     *
     * @param response 响应，数据载体
     * @throws IOException 文件上传IO异常
     */
    @ApiOperation("下载数据字典Excel表")
    @GetMapping("/downLoad")
    public void downLoad(HttpServletResponse response) throws IOException {
        dictService.downLoad(response);
    }

    /**
     * 将前端上传的Excel表下载解析，更新到数据库中
     *
     * @param file 上传的Excel文件
     * @return 上传成功，响应20000状态码
     */
    @ApiOperation("上传数据字典Excel表")
    @PostMapping("/upLoad")
    public Result upLoad(MultipartFile file) throws IOException {
        dictService.upLoad(file);
        return Result.ok();
    }

    /**
     * 查询省市区
     *
     * @param value
     * @return
     */
    @ApiOperation("根据value查询name")
    @GetMapping("/feign/getNameByValue/{value}")
    public String getNameByValue(@PathVariable String value) {

        return dictService.getNameByValue(value);
    }

    /**
     * 查询医院等级
     *
     * @param parentCode 查询医院等级的id
     * @param value      在医院等级中，根据value查询出对应的等级
     * @return
     */
    @ApiOperation("根据parentCode和value查询name")
    @GetMapping("/feign/getNameByParentCodeAndValue/{parentCode}/{value}")
    public String getNameByParentCodeAndValue(@PathVariable String parentCode, @PathVariable String value) {

        return dictService.getNameByParentCodeAndValue(parentCode, value);
    }

    @ApiOperation("根据parentCode查询dict")
    @GetMapping("/findDictByParentCode/{parentCode}")
    public Result findDictByParentCode(@PathVariable String parentCode) {
        List<Dict> dictList = dictService.findDictByParentCode(parentCode);
        return Result.ok().data("list", dictList);
    }
}

