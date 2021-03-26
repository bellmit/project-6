package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.service.XyOperationLogService;
import com.miguan.xuanyuan.vo.XyOperationLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author kangkunhuang
 * @Description 操作日志
 * @Date 2021/1/21
 **/
@Api(value = "操作日志控制层", tags = {"操作日志接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/operationLog")
public class XyOperationLogController {

    @Resource
    private XyOperationLogService service;


    @ApiOperation("分页查询操作日志列表")
    @GetMapping("/pageList")
    public ResultMap<PageInfo<XyOperationLogVo>> pageList(@ApiParam("开始日期") String startDate,
                                                          @ApiParam("结束日期") String endDate,
                                                          @ApiParam("操作平台 1 前台 2 后台") Integer operationPlat,
                                                          @ApiParam("操作路径") String pathName,
                                                          @ApiParam("操作行为 1 增 2 删 3 改") Integer type,
                                                          @ApiParam("关键字") String keyword,
                                                          @ApiParam(value="页码", required=true) Integer pageNum,
                                                          @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        return ResultMap.success(service.pageList(startDate,endDate, operationPlat ,pathName ,type ,keyword , pageNum, pageSize));
    }

}
