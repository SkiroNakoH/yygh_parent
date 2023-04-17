package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-11
 */
public interface DictService extends IService<Dict> {

    List<Dict> findListByParentId(Long parentId);

    void downLoad(HttpServletResponse response) throws IOException;

    void upLoad(MultipartFile srcFile) throws IOException;

    String getNameByValue(String value);

    String getNameByParentCodeAndValue(String parentCode, String value);


    List<Dict> findDictByParentCode(String parentCode);

}
