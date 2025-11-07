package com.chiho.archBI.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chiho.archBI.common.BaseResponse;
import com.chiho.archBI.common.ErrorCode;
import com.chiho.archBI.common.ResultUtils;
import com.chiho.archBI.constant.CommonConstant;
import com.chiho.archBI.exception.BusinessException;
import com.chiho.archBI.exception.ThrowUtils;
import com.chiho.archBI.model.dto.chart.ChartAddRequest;
import com.chiho.archBI.model.dto.chart.ChartQueryRequest;
import com.chiho.archBI.model.dto.chart.GenChartByAiRequest;
import com.chiho.archBI.model.entity.Chart;
import com.chiho.archBI.model.entity.User;
import com.chiho.archBI.service.ChartService;
import com.chiho.archBI.service.UserService;
import com.chiho.archBI.utils.ExcelUtils;
import com.chiho.archBI.utils.SqlUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Random;
import java.util.UUID;

/**
 * 帖子接口
 *
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private UserService userService;

    @Resource
    private ChartService chartService;

    // 引入谷歌的JSON解析处理其
    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest 图表新增请求DTO
     * @param request HTTP请求
     * @return 返回添加成功的图表ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        // 当传入的图表数据为空时，抛出异常
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 智能分析接口
     * @param multipartFile http传输的多部分文件流
     * @param genChartByAiRequest 文件上传请求DTO
     * @param request 封装HTTP请求信息的工具接口
     * @return 返回上传成功的文件路径
     */
    @PostMapping("/gen")
    public BaseResponse<String> genChartByAi
    (
            @RequestPart("file") MultipartFile multipartFile,
            GenChartByAiRequest genChartByAiRequest,
            HttpServletRequest request
    ) {
        // 1.获取request当中的值
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 2.校验request值（异常处理）
        // 如果分析目标为空
        ThrowUtils.throwIf(StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"分析目标过长");
        // 如果名称不为空，并且名称长度大于100，抛出异常
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR,"名称过长");

        // xlsx转换成csv
        String result = ExcelUtils.excelToCSV(multipartFile);
        return ResultUtils.success(result);

//        User loginUser = userService.getLoginUser(request);
//        // 生成8个字母的字符串，作为UUID
//        String uuid = RandomStringUtils.randomAlphabetic(8); // String uuid = UUID.randomUUID().toString();
//        String filename = uuid + "-" + multipartFile.getOriginalFilename();
//        File file = null;
//        try{
//            // 返回可访问地址
//            return ResultUtils.success("");
//        }catch (Exception e){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
//        }finally {
//            if (file != null){
//                boolean delete  = file.delete();
//                if (!delete){
//                    log.error("file delete error");
//                }
//            }
//        }
    }

    /**
     * 根据传入的查询参数对象，构建Mybatis-Plus框架的查询条件构造器
     * @param chartQueryRequest chart表查询DTO
     * @return chart表的查询包装器
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest){
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        // 判断非空
        if (chartQueryRequest == null){
            return queryWrapper;
        }
        // 提取请求DTO中的值
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        // DTO从PageRequest中继承而来的字段，用于排序
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 匹配查询逻辑
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }

}
