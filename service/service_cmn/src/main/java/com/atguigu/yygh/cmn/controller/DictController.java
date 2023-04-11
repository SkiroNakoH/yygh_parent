package com.atguigu.yygh.cmn.controller;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin //解决跨域访问
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("字典列表查询")
    @GetMapping({"/findListByParentId/{parentId}","/findListByParentId"})
    public Result findListByParentId(@ApiParam("父Id") @PathVariable(required = false) Long parentId) {
        if (parentId == null) {
            parentId = 1L;
        }

        List<Dict> dictList = dictService.findListByParentId(parentId);
        return Result.ok().data("dictList",dictList);
    }

}

