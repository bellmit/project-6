package com.miguan.ballvideo.controller;

import com.google.common.collect.Maps;
import com.miguan.ballvideo.service.dsp.nadmin.interceptor.LoginAuth;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.dsp.PageUtil;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;
import com.miguan.ballvideo.entity.dsp.Page;
import com.miguan.ballvideo.entity.dsp.PageVo;
import com.miguan.ballvideo.service.dsp.DspAdvUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author suhongju
 */

@Slf4j
@Api(value="Dsp广告主管理 Controller",tags={"Dsp广告主管理接口"})
@RestController
@RequestMapping("/api/idea/AdvertUser")
public class DspAdvUserController {

    @Resource
    private DspAdvUserService dspAdvUserService;

    @ApiOperation("获取广告主列表")
    @PostMapping("/List")
    @LoginAuth
    public ResultMap<Page> getList(@ApiParam("分页实体类") @ModelAttribute PageVo pageVo,
                                   @ApiParam(value = "广告主名称") String name,
                                   @ApiParam(value = "行业类型") String type,
                                   @ApiParam(value = "广告主id，多个‘,’隔开") String ad_user_id) {
        try {
            if(pageVo.getPage() == 0){
                return ResultMap.error("缺少必要的当前页数");
            }
            if(pageVo.getPage_size() == 0){
                return ResultMap.error("缺少必要的每页数量");
            }
            List<Map<String,Object>> lst = dspAdvUserService.getList(name,type,ad_user_id);
            Page page = PageUtil.setPageable(lst,pageVo);
            return ResultMap.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取广告主详情")
    @PostMapping("/Info")
    @LoginAuth
    public ResultMap<Map<String,Object>> getInfo(@ApiParam(value = "广告主id") String ad_user_id) {
        try {
            if(StringUtil.isEmpty(ad_user_id)){
                return ResultMap.error("缺少必要的广告主id");
            }
            Map<String,Object> map = dspAdvUserService.getInfo(ad_user_id);
            return ResultMap.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("新增、编辑广告主")
    @PostMapping("/AddEdit")
    @LoginAuth
    public ResultMap advAddEdit(@ApiParam("广告主实体类") @ModelAttribute IdeaAdvertUserVo userVo) {
        try {
            if(StringUtil.isEmpty(userVo.getName())){
                return ResultMap.error("缺少必要的公司名称");
            }
            String adUserId = dspAdvUserService.advAddEdit(userVo);
            Map<String,String> map = Maps.newHashMap();
            map.put("ad_user_id",adUserId);
            return ResultMap.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }


}
