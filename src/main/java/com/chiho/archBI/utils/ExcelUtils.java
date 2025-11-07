package com.chiho.archBI.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理excel文件的工具类
 */
public class ExcelUtils {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * Excel文件数据压缩成CSV文件数据
     * @param multipartFile 传入的excel多部分文件
     * @return 返回字符串
     */
    public static String excelToCSV(MultipartFile multipartFile){
        //        File file = null;
        //        try{
        //            // 获取xlsx文件（"classpath:WebData.xlsx" 中间不能有空格，不然会读取不到文件）
        //            file = ResourceUtils.getFile("classpath:WebData.xlsx");
        //        } catch (FileNotFoundException e) {
        //            log.error("xlsx文件获取失败：{}",file);
        //        }

        // 将xlsx文件解析成列表形式
        List<Map<Integer, String>> list = null;
        try{
           list =  EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        }catch (IOException e){
            log.error("表格处理失败",e);
        }

        // 判断列表数据是否为空
        if (CollUtil.isEmpty(list)) return "";
        log.info("xlsx文件数据转换成list列表：{}",list);
        // 转换成csv
        StringBuilder stringBuilder = new StringBuilder();
        // 从转化后的list列表中读取表头（第一行）
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headerList = headerMap
                .values()
                .stream().
                filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());
        // 拼接表头数据
        stringBuilder.append(StringUtils.join(headerList,",")).append("\n");

        // 读取数据（读取完表头之后从第一行开始读取）
        for (int i = 1; i < list.size(); i++){
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap
                    .values()
                    .stream().
                    filter(ObjectUtils::isNotEmpty)
                    .collect(Collectors.toList());
            // 拼接表尾数据（广义表）
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }
        log.info("转换成功后的csv数据：{}",stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static void main(String[] args){
    }
}
