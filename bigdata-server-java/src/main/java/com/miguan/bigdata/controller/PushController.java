package com.miguan.bigdata.controller;

import com.miguan.bigdata.controller.base.BaseController;
import com.miguan.bigdata.dto.PushDto;
import com.miguan.bigdata.service.ManualService;
import com.miguan.bigdata.service.PushLdService;
import com.miguan.bigdata.service.PushVideoService;
import com.miguan.bigdata.vo.PushPredictVo;
import com.miguan.bigdata.vo.PushResultVo;
import com.miguan.bigdata.vo.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "推送相关接口", tags = {"推送"})
@Slf4j
@RestController
public class PushController extends BaseController {

    @Resource
    private ManualService pushService;
    @Resource
    private PushLdService pushLdService;
    @Resource
    private PushVideoService pushVideoService;

    /**
     * 手动推送预估人数
     * @param catids
     * @return
     */
    @PostMapping("/api/push/predictCount")
    public ResultMap<PushPredictVo> predictCount(String catids) {
        if (StringUtils.isEmpty(catids)) {
            return ResultMap.error("参数catids不能为空");
        }

        Long count = pushService.countPush(null, catids);
        return ResultMap.success(new PushPredictVo(count));
    }

    @ApiOperation(value = "自动推送接口")
    @PostMapping("/api/push/findAutoPushList")
    public List<PushResultVo> findLdAutoPushList(@ModelAttribute PushDto pushDto) {

        List<PushResultVo> list = null;
        if(pushDto.getProjectType() == 1){
            // 如果包名是xld，查询时的包名为com.mg.phonecall， 返回的包名为xld
            boolean isXld = pushDto.getPackageName().equals("xld");
            if (isXld) {
                pushDto.setPackageName("com.mg.phonecall");
            }

            list = pushLdService.findLdAutoPushList(pushDto);

            if (isXld) {
                list.stream().forEach(l ->{
                    l.setPackageName("xld");
                });
            }
        } else {
            list = pushVideoService.findAutoPushList(pushDto);
        }
        return list;
    }
}
