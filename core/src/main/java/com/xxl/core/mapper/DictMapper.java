package com.xxl.core.mapper;

import com.xxl.core.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.core.to.ExcelDictDTO;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);
}
