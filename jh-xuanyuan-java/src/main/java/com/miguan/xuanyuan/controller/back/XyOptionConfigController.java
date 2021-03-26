package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.XyOptionConfig;
import com.miguan.xuanyuan.service.XyOptionConfigService;
import com.miguan.xuanyuan.vo.XyOptionConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author kangkunhuang
 * @Description 选项配置
 * @Date 2021/1/21
 **/
@Api(value = "选项配置控制层", tags = {"选项配置接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/xyOptionConfig")
public class XyOptionConfigController {

    @Resource
    private XyOptionConfigService service;


    @ApiOperation("分页查询选项配置列表")
    @GetMapping("/pageList")
    public ResultMap<PageInfo<XyOptionConfigVo>> pageList(@ApiParam("中文字段") String keyword,
                                                          @ApiParam("状态,1启用，0禁用") Integer status,
                                                          @ApiParam("排序字段，字段+排序方式，例如：configName asc(参数编号按升序)，configName desc(参数编号按降序)") String sort,
                                                          @ApiParam(value="页码", required=true) Integer pageNum,
                                                          @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        return ResultMap.success(service.pageList(keyword,status, sort , pageNum, pageSize));
    }

    @ApiOperation("新增、编辑选项配置")
    @PostMapping("/save")
    @LogInfo(pathName="后台-系统字典-新增、编辑选项配置",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap save(@ApiParam("选项配置实体类") @RequestBody XyOptionConfig xyOptionConfig) {
        try {
            //校验
            if(StringUtils.isEmpty(xyOptionConfig.getConfigCode())){
                return ResultMap.error("必须传入英文字段");
            }
            if(StringUtils.isEmpty(xyOptionConfig.getConfigName())){
                return ResultMap.error("必须传入中文字段");
            }
            if(xyOptionConfig.getStatus() == null){
                return ResultMap.error("必须传入状态");
            }
            //添加选项配置
            if(xyOptionConfig.getNote() == null){  //如果为空,填入''默认值。
                xyOptionConfig.setNote("");
            }
            service.save(xyOptionConfig);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("查询选项配置信息")
    @GetMapping("/info")
    public ResultMap<XyOptionConfig> info(@ApiParam("选项配置实体类") Long id) {
        try {
            if(id == null){
                return ResultMap.error("必须传入选项配置主键");
            }

            return ResultMap.success(service.findById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

}
