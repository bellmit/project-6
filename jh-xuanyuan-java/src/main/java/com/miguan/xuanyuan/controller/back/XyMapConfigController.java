package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.XyMapConfig;
import com.miguan.xuanyuan.service.XyMapConfigService;
import com.miguan.xuanyuan.vo.XyMapConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author kangkunhuang
 * @Description 参数配置
 * @Date 2021/1/21
 **/
@Api(value = "参数配置控制层", tags = {"参数配置接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/xyMapConfig")
public class XyMapConfigController {

    @Resource
    private XyMapConfigService service;


    @ApiOperation("分页查询参数配置列表")
    @GetMapping("/pageList")
    public ResultMap<PageInfo<XyMapConfigVo>> pageList(@ApiParam("参数名称") String keyword,
                                                       @ApiParam("状态,1启用，0禁用") Integer status,
                                                       @ApiParam("排序字段，字段+排序方式，例如：configKey asc(参数编号按升序)，configKey desc(参数编号按降序)") String sort,
                                                       @ApiParam(value="页码", required=true) Integer pageNum,
                                                       @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        return ResultMap.success(service.pageList(keyword,status, sort , pageNum, pageSize));
    }

    @ApiOperation("新增、编辑参数配置")
    @PostMapping("/save")
    @LogInfo(pathName="后台-平台配置-新增、编辑参数配置",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_SAVE)
    public ResultMap save(@ApiParam("参数配置实体类") @RequestBody XyMapConfig xyMapConfig) {
        try {
            if(StringUtils.isEmpty(xyMapConfig.getConfigKey())){
                return ResultMap.error("必须传入参数编号！");
            }
            if(StringUtils.isEmpty(xyMapConfig.getConfigName())){
                return ResultMap.error("必须传入参数名称！");
            }
            if(StringUtils.isEmpty(xyMapConfig.getConfigVal())){
                return ResultMap.error("必须传入参数值！");
            }
            if(xyMapConfig.getStatus() == null){
                return ResultMap.error("必须传入状态！");
            }

            //添加参数配置
            if(xyMapConfig.getNote() == null){  //如果为空,填入''默认值。
                xyMapConfig.setNote("");
            }
            service.save(xyMapConfig);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("查询参数配置信息")
    @GetMapping("/info")
    public ResultMap<XyMapConfig> info(@ApiParam("参数配置实体类") Long id) {
        try {
            if(id == null){
                return ResultMap.error("必须传入参数配置主键");
            }

            return ResultMap.success(service.findById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

}
