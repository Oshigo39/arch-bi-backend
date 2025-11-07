package com.chiho.archBI.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * AI图表上传分析请求
 * 用户上传文件时，给ai的辅助分析信息
 */
@Data
public class GenChartByAiRequest implements Serializable {

    /**
     * 图表名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图标类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}