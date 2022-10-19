package com.xxl.core.service;

import com.xxl.core.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.core.to.ExcelDictDTO;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface DictService extends IService<Dict> {

    List<Dict> listWithTree();


    void batchImport(InputStream inputStream);

    List<ExcelDictDTO> listDictData();


    List<Dict> findByDictCode(String dictCode);

    String getNameByParentDictCodeAndValue(String dictCode, Integer value);
}
