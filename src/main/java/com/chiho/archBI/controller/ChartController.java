package com.chiho.archBI.controller;

import com.chiho.archBI.common.BaseResponse;
import com.chiho.archBI.common.ErrorCode;
import com.chiho.archBI.common.ResultUtils;
import com.chiho.archBI.exception.BusinessException;
import com.chiho.archBI.exception.ThrowUtils;
import com.chiho.archBI.model.dto.chart.ChartAddRequest;
import com.chiho.archBI.model.entity.Chart;
import com.chiho.archBI.model.entity.User;
import com.chiho.archBI.service.ChartService;
import com.chiho.archBI.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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




}
