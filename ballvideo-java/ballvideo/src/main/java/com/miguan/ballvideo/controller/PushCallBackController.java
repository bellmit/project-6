package com.miguan.ballvideo.controller;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.aop.UserOperate;
import com.miguan.ballvideo.common.constants.OperateConstant;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.entity.PushArticleSendResult;
import com.miguan.ballvideo.service.PushArticleSendResultService;
import com.miguan.message.push.utils.huawei.messaging.CallBackBody;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息推送回调接口
 * @author shixh
 * */
@Slf4j
@Api(value="消息推送回调接口",tags={"消息推送回调接口"})
@RequestMapping("/api/push")
@RestController
public class PushCallBackController {

    @Resource
    private PushArticleSendResultService pushArticleSendResultService;

  /**
   * 华为推送（需要设置成https请求）
   * @param statuses
   * @return
   */
  @UserOperate(name= OperateConstant.huaweiCallBack,business="华为回调日志记录，调试用",recordParams=true)
  @PostMapping("/huaweiCallBack")
  public Map huaweiCallBack(@RequestBody String statuses) {
      //status：0-成功，2-应用已卸载，5-消息推送的目标用户Push token在用户手机本地不存在，6-用户在应用内设置禁止展示消息，10-消息丢弃标识，15-离线消息替换，27-收到App的Push透传消息，拉起服务失败本地缓存，102-消息频控丢弃，201-无效消息发送管控
      //对status=2、5、6、10、201做过滤处理，减少对这些用户的无效推送。
        log.info(statuses);
        HashMap<String,String> hashMap = JSON.parseObject(statuses, HashMap.class);
        String json = JSON.toJSONString(hashMap.get("statuses"));
        List<CallBackBody> bodys = JSON.parseArray(json,CallBackBody.class);
        for(CallBackBody body:bodys){
            String requestId = body.getRequestId();
            int status = body.getStatus();
            if(0==status){
                //记录推送成功数
                PushArticleSendResult sendResult= pushArticleSendResultService.findByPushChannelAndBusinessId(PushChannel.HuaWei.name(),requestId);
                if(sendResult!=null){
                    sendResult.setGetNum(sendResult.getGetNum()+1);
                    pushArticleSendResultService.save(sendResult);
                }
            }
        }
        Map map = new HashMap<>();
        map.put("errno","0");//官网没有查到errno返回的值描述 add shixh1225
        map.put("errmsg","success");//官网没有查到errmsg返回的值描述 add shixh1225
        return map;
    }

}
