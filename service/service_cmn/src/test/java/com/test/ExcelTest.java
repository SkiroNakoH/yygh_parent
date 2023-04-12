package com.test;

import com.alibaba.excel.EasyExcel;
import com.listener.ExcelListener;
import com.pojo.DemoData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ExcelTest {


    //java 2 excel
    @Test
    public void writeExcel(){

       List<DemoData> list = new ArrayList<>();

       list.add(new DemoData(1L,"张三",18));
       list.add(new DemoData(2L,"李四",19));
       list.add(new DemoData(3L,"王五",20));
       list.add(new DemoData(4L,"赵六",21));

        String fileName = "C:\\Users\\SkiroNako\\Desktop\\demo.xlsx";

        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class).sheet("模板").doWrite(list);
    }


    //excel 2 java
    @Test
    public void readExcel(){

        String fileName = "C:\\Users\\SkiroNako\\Desktop\\demo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new ExcelListener()).sheet().doRead();
    }

}
