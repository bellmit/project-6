package com.miguan.xuanyuan.controller.back;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import com.miguan.xuanyuan.service.XyOptionConfigService;
import com.miguan.xuanyuan.service.XyOptionItemConfigService;
import com.miguan.xuanyuan.vo.XyOptionItemConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 选项配置项
 * @Date 2021/1/21
 **/
@Api(value = "选项配置项控制层", tags = {"选项配置项接口"})
@RestController
@Slf4j
@RequestMapping("/api/back/xyOptionItemConfig")
public class XyOptionItemConfigController {

    @Resource
    private XyOptionItemConfigService service;

    @Resource
    private XyOptionConfigService xyOptionConfigService;

    @ApiOperation("查询配置项列表")
    @GetMapping("/findByConfigCode")
    public ResultMap<XyOptionItemConfigVo> pageList(@ApiParam("中文字段") String codeConfig) {
        List<XyOptionItemConfig> itemConfigs = service.findByConfigCode(codeConfig);
        return ResultMap.success(new XyOptionItemConfigVo(itemConfigs,codeConfig));
    }

    @ApiOperation("新增、编辑选项配置项")
    @PostMapping("/save")
    @LogInfo(pathName="后台-系统字典-新增、编辑选项配置项",plat= XyConstant.BACK_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap save(@ApiParam("选项配置项实体类") @RequestBody XyOptionItemConfigVo xyOptionItemConfigVo) {
        try {
            if(StringUtils.isEmpty(xyOptionItemConfigVo.getConfigCode())){
                return ResultMap.error("请输入必传的配置参数！");
            }

            boolean b = xyOptionConfigService.judgeExistCode(xyOptionItemConfigVo.getConfigCode());
            if(!b){
                return ResultMap.error("不存在该选项配置！");
            }
            //后面换到初始化那
            if(CollectionUtils.isNotEmpty(xyOptionItemConfigVo.getItems())){ //初始化默认值
                judgeItems(xyOptionItemConfigVo.getItems());
                xyOptionItemConfigVo.getItems().forEach(item -> item.setConfigCode(xyOptionItemConfigVo.getConfigCode()));
            }
            //添加选项配置项
            service.saveBatch(xyOptionItemConfigVo.getConfigCode(),xyOptionItemConfigVo.getItems());
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    private void judgeItems(List<XyOptionItemConfig> items) throws ValidateException{
        for (XyOptionItemConfig config:items) {
            if(StringUtils.isEmpty(config.getItemKey())){
                throw new ValidateException("请输入必传的key！");
            }
            if(StringUtils.isEmpty(config.getItemVal())){
                throw new ValidateException("请输入必传的value！");
            }
        }
    }

    @ApiOperation("查询选项配置项信息")
    @GetMapping("/info")
    public ResultMap<XyOptionItemConfig> info(@ApiParam("选项配置项实体类") Long id) {
        try {
            if(id == null){
                return ResultMap.error("必须传入选项配置项主键");
            }

            return ResultMap.success(service.findById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

}
