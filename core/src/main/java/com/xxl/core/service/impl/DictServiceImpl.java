package com.xxl.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.core.entity.Dict;
import com.xxl.core.listenner.ExcelDictDTOListener;
import com.xxl.core.mapper.DictMapper;
import com.xxl.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.core.to.ExcelDictDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> listWithTree() {
        List<Dict> dicts = baseMapper.selectList(null);
        List<Dict> dictList = dicts.stream().filter(dict -> {
            return dict.getParentId() == 1;
        }).map(dict -> {
            dict.setChildren(getChildren(dict, dicts));
            return dict;
        }).collect(Collectors.toList());
        return dictList;
    }
    private List<Dict> getChildren(Dict root, List<Dict> all) {
        List<Dict> dictList = all.stream().filter(dict -> {
            //当前这个节点id 与总节点的遍历的每一个节点相等即可
            return root.getId().equals(dict.getParentId());
        }).map(dict -> {
            dict.setChildren(getChildren(dict, all));
            return dict;
        }).collect(Collectors.toList());
        return dictList;
    }

    @Override
    public void batchImport(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dicts = baseMapper.selectList(null);
        //创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表
        List<ExcelDictDTO> collect = dicts.stream().map(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            return excelDictDTO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {

        Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
        List<Dict> dictList = baseMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", dict.getId()));
        return dictList;
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", dictCode));
        Dict one = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", dict.getId()).eq("value", value));
        return one.getName();


    }

}
