package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;


public class UploadDictEeVoListener implements ReadListener<DictEeVo> {
    private DictMapper dictMapper;

    public UploadDictEeVoListener(DictMapper baseMapper) {
        this.dictMapper = baseMapper;
    }

    //上传excel表，更新数据库
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //dictEeVo 2 dict
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);

        //查询数据库中是否存在该dict
        Dict searchDict = dictMapper.selectById(dictEeVo.getId());

        if (searchDict == null) {
            //db中无该数据，添加
            dictMapper.insert(dict);
        } else {
            //db中有该数据，修改
            dictMapper.updateById(dict);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //上传结束
    }
}
