package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.Apps;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.service.AppsService;
import com.miguan.ballvideo.vo.AppsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@Api(value = "上传app列表controller",tags={"上传app列表"})
@RequestMapping("/api/apps")
public class AppsController {

    @Resource
    private AppsService appsService;

    /**
     * 上传app列表
     * @param appsVo
     * @return
     */
    @ApiOperation(value = "上传app列表")
    @PostMapping("/uploadApps")
    public ResultMap uploadApps(@RequestBody AppsVo appsVo,
                                @RequestHeader(value = "Public-Info", required = false) String publicInfo) {
        if (StringUtils.isBlank(appsVo.getDeviceId())) {
            log.error("上传app列表结果：设备Id为空");
            return ResultMap.error("设备Id不能为空");
        }
        if (StringUtils.isBlank(appsVo.getAppPackage())) {
            log.error("上传app列表结果：包名为空");
            return ResultMap.error("包名不能为空");
        }
        List<Apps> appsList = appsVo.getAppsList();
        if (CollectionUtils.isEmpty(appsList) || StringUtils.isEmpty(appsList.get(0).getPackageName())) {
            return ResultMap.error("无APP安装列表");
        }
        if (StringUtils.isEmpty(publicInfo)) {
            log.error("上传app列表结果：Public-Info为空");
            return ResultMap.error("Public-Info不能为空");
        }
        PublicInfo pbInfo  = new PublicInfo(publicInfo);
        appsVo.setDistinctId(pbInfo.getDistinctId());
        appsService.saveToMongodb(appsVo);
        return ResultMap.success();
    }

    @ApiOperation(value = "创建mongodb索引")
    @GetMapping("/createIndex")
    public ResultMap createIndex() {
        appsService.createIndex();
        return ResultMap.success();
    }

}
