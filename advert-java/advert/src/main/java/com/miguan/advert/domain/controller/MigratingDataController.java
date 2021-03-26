package com.miguan.advert.domain.controller;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.MigratingDataService;
import com.miguan.advert.domain.vo.result.AppInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "数据迁移相关",tags = {"数据迁移相关"})
@RestController
@RequestMapping("/api/migrating_data")
public class MigratingDataController {

    @Resource
    private MigratingDataService migratingDataService;

    @ApiOperation("广告配置信息数据迁移")
    //@PostMapping("/migratingAdConfigData")
    public ResultMap migratingAdConfigData () {
        return migratingDataService.migratingAdConfigData();
    }

    @ApiOperation("广告配置信息数据迁移,仅添加默认分组")
    //@PostMapping("/migratingAdConfigDataDefalut")
    public ResultMap migratingAdConfigDataDefalut () {
        return migratingDataService.migratingAdConfigDataDefalut();
    }

    @ApiOperation("删除AB默认实验以及AB实验")
    //@PostMapping("/moveAbConfigData")
    public ResultMap moveAbConfigData (Integer positionId,String msg) {
        if(positionId == null && StringUtils.isEmpty(msg)){
            return ResultMap.error("positionId和msg至少有一个");
        }
        if(positionId == null && !"删除全部".equals(msg)){
            return ResultMap.error("必须传入positionId");
        }
        boolean b = false;
        if("删除全部".equals(msg)){
            b = true;
        }
        return migratingDataService.moveAbConfigData(positionId,b);
    }


    @ApiOperation("删除还原")
    //@PostMapping("/rollback")
    public ResultMap rollback (String msg) {
        if(StringUtils.isEmpty(msg)){
            return ResultMap.error("必须输入回滚指令");
        }
        if(!"数据回滚121314".equals(msg)){
            return ResultMap.error("回滚指令错误");
        }
        return migratingDataService.rollback();
    }
}
