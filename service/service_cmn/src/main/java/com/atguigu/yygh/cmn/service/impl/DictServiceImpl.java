package com.atguigu.yygh.cmn.service.impl;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-11
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> findListByParentId(Long parentId) {
        //查询
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", parentId);

        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);

        //是否有孩子
/*        for (Dict dict : dictList) {
            hasChildren(dict);
        }*/
        dictList.forEach(this::hasChildren);

        return dictList;
    }

    //是否有孩子
    private void hasChildren(Dict dict) {
        //查看该dict是否有孩子
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", dict.getId());

        if(baseMapper.selectCount(queryWrapper) > 0){
            dict.setHasChildren(true);
        }
    }
}
