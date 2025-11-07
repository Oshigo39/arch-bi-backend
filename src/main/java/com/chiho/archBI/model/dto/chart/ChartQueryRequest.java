package com.chiho.archBI.model.dto.chart;

import com.chiho.archBI.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;;

/**
 * 查询DTO请求
 * 指定用户可以使用什么字段查询Chart表格
 */
@EqualsAndHashCode(callSuper = true)    // 该注解提供equals()、hashCode()方法，指定为true时，先调用父类的
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     * 雪花算法生成的ID
     */
    private Long id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}