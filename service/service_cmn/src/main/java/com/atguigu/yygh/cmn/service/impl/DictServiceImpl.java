package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.UploadDictEeVoListener;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
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


    /**
     *  @Cacheable(value = "dict",key = "'findListByParentId'+#parentId")
     *  第一次去db查询，携带数据返回，并将查询的值放入缓存中；
     *  第二次即以上查询，先去缓存区中查询是否有该缓存，如果有，直接返回数据，如果没有，重复操作一
     *
     * 缓存命名方式:  value:key   =>  例如: dict:findListByParentId:1
     *
     * #parentId 展开孩子时，需要使用当前id作为父id,去数据库查询。所以缓存命名值不能写死
     */
    @Cacheable(value = "dict",key = "'findListByParentId'+#parentId")
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

    //让前端下载excel
    @Override
    public void downLoad(HttpServletResponse response) throws IOException {
        List<Dict> dictList = baseMapper.selectList(null);

        List<DictEeVo> dictEeVoList = new ArrayList<>();

        //转换
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);

            dictEeVoList.add(dictEeVo);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 这里需要设置不关闭流
        EasyExcel.write(response.getOutputStream(), DictEeVo.class).autoCloseStream(Boolean.FALSE).sheet("数据字典工作表")
                .doWrite(dictEeVoList);
    }

    @Override
    public void upLoad(MultipartFile srcFile) throws IOException {
        EasyExcel.read(srcFile.getInputStream(), DictEeVo.class, new UploadDictEeVoListener(baseMapper)).sheet().doRead();
    }
}
