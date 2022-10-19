package com.xxl.core.listenner;

import com.alibaba.excel.context.AnalysisContext;

import com.alibaba.excel.event.AnalysisEventListener;
import com.xxl.core.mapper.DictMapper;
import com.xxl.core.to.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j

@NoArgsConstructor   // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {
    //每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
    private static final int BATCH_COUNT = 5;
    List<ExcelDictDTO> list = new ArrayList();
    //假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
    //如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
    private DictMapper dictMapper;
    //传入mapper对象
    public ExcelDictDTOListener(DictMapper dictMapper){
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        this.dictMapper=dictMapper;
    }

    //这个每一条数据解析都会来调用
    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("解析一条数据: {}" ,excelDictDTO);
        list.add(excelDictDTO);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData() ; //批量插入
            // 存储完成清理 list
            list.clear();
        }
    }
    private  void saveData(){
        dictMapper.insertBatch(list);  //批量插入
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("数据解析完成");
        saveData() ; //批量插入
    }
}
