package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.TaskSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消息推送查询接口
 * @Author shixh
 * @Date 2019/12/24
 **/
@Slf4j
@Api(value="消息推送查询接口",tags={"消息推送查询接口"})
@RestController
@RequestMapping("/api/task")
public class TaskSearchController {

    @Resource
    private TaskSearchService taskSearchService;

  /**
   * 根据taskID查询统计结果

   返回参数说明友盟-安卓：
   |task_id-任务ID  |
   |status-消息状态: 0-排队中, 1-发送中，2-发送完成，3-发送失败，4-消息被撤销，5-消息过期, 6-筛选结果为空，7-定时任务尚未开始处理 |
   |sent_count-消息收到数  |
   |open_count-打开数  |
   |dismiss_count-忽略数  |
   返回参数说明友盟-IOS：
   |task_id-任务ID  |
   |status-消息状态: 0-排队中, 1-发送中，2-发送完成，3-发送失败，4-消息被撤销，5-消息过期, 6-筛选结果为空，7-定时任务尚未开始处理 |
   |total_count-投递APNs设备数  |
   |sent_count-APNs返回SUCCESS的设备数  |
   |open_count-打开数  |
   * @param pushChannel
   * @param appPackage
   * @param taskId
   * @return
   */
  @ApiOperation("推送结果查询（VIVO，友盟（IOS/android））")
  @GetMapping("/searchByTaskId/{pushChannel}/{appPackage}")
  public ResultMap searchByTaskId(
      @PathVariable(value = "pushChannel") String pushChannel,
      @PathVariable(value = "appPackage") String appPackage,
      String taskId) throws Exception{
        /*if (StringUtils.isBlank(pushChannel)
                || StringUtils.isBlank(taskId)
                || StringUtils.isBlank(appPackage)){
            return ResultMap.error("参数异常！");
        }
        PushChannel channel = PushChannel.val(pushChannel);
        switch (channel){
            case OPPO:
                return ResultMap.error("OPPO不支持通过接口查询推送统计，只能登陆推送平台查看");
            case YouMeng:
                return taskSearchService.searchYouMeng(taskId,appPackage);
            case XiaoMi:
                return taskSearchService.searchXiaoMi(taskId,appPackage);
            case VIVO:
                return taskSearchService.searchVivo(taskId,appPackage);
            case HuaWei:
                return ResultMap.error("华为不支持通过接口查询推送统计，需要配置回调地址，并且设置HTTPS请求");
        }
        return ResultMap.error();*/
      return ResultMap.success();
    }
}
