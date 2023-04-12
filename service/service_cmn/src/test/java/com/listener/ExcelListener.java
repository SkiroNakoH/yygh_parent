package com.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.pojo.DemoData;

import java.util.Map;



public class ExcelListener  implements ReadListener<DemoData> {


    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        System.out.println("表头：");
        System.out.print(headMap.get(0).getStringValue() + "\t");
        System.out.print(headMap.get(1).getStringValue() + "\t");
        System.out.println(headMap.get(2).getStringValue());
    }

    @Override
    public void invoke(DemoData demoData, AnalysisContext analysisContext) {
        System.out.print("表内容: ");
        System.out.println("demoData = " + demoData);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("监听器结束");
    }
}
