package com.miguan.xuanyuan.service.third.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.xuanyuan.common.enums.ThirdPlatEnum;
import com.miguan.xuanyuan.common.util.RobotUtil;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.service.UserService;
import com.miguan.xuanyuan.service.third.ChuanShanJiaService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import com.miguan.xuanyuan.vo.ThirdPlatDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 第三方快手Service
 */
@Slf4j
@Service
public class ChuanShanJiaServiceImpl implements ChuanShanJiaService {

    @Resource
    private RestTemplate restTemplate;
//    private String cookie = "Hm_lvt_64d8d9aaa00cd643749093c0625403c3=1601001452; pangle-i18n=zh; n_mh=9-mIeuD4wZnlYrrOvfzG3MuT6aQmCUtmr8FxV8Kl8xY; ttwid=1%7CTeSUXCBMqhOsYWfeYdVloEE0hDe6HaKr_tPVQexi-zg%7C1611541725%7C6ba0f9a316b06e66687991cfb330528de7bf77f4e332f16be40c8337d4023fe6; passport_csrf_token=38a13e6f9d6dfed7ddd18ee51e2c93f1; passport_csrf_token_default=38a13e6f9d6dfed7ddd18ee51e2c93f1; Hm_lvt_eaa57ca47dacb4ad4f5a257001a3457c=1614219734,1614305740,1614389317,1614564416; Hm_lvt_ff76cafb7653fe92a16e2025d769a918=1614045860,1614321976,1614408737,1614566589; Hm_lpvt_ff76cafb7653fe92a16e2025d769a918=1614566589; session=eyJjc3JmX3Rva2VuIjp7IiBiIjoiTmpJMU1tUTRNbUUxT1RrNU1XTmpNMlJtTUdVME56SmtZVEkyWXpWaE9EUm1NV016TW1VMk1RPT0ifX0.YDxUvg.thf6-AK9JHVQf_mcTnHfvcgUSiY; s_v_web_id=klpz9egq_wIE0udmS_fz8a_4hLn_B1wp_EWZ80N7Zyf2O; odin_tt=e40279a0c7964034b05d16c7f94246327e2c18af31a60e0c56286c2f8effd118e4e04a2b097c270c696b787511e0b69fe2fa3c6dc79ebe1e07f19137db759fbf; sid_guard=d72b8d5dd60beee37edf2aec9557e7dd%7C1614566628%7C5184000%7CFri%2C+30-Apr-2021+02%3A43%3A48+GMT; uid_tt=b63a82f01a00d391a2bdab3191285635; uid_tt_ss=b63a82f01a00d391a2bdab3191285635; sid_tt=d72b8d5dd60beee37edf2aec9557e7dd; sessionid=d72b8d5dd60beee37edf2aec9557e7dd; sessionid_ss=d72b8d5dd60beee37edf2aec9557e7dd; gftoken=ZDcyYjhkNWRkNnwxNjE0NTY2NjI4OTd8fDAGBgYGBgY; MONITOR_WEB_ID=10196; Hm_lpvt_eaa57ca47dacb4ad4f5a257001a3457c=1614566635";
    private String reportUrl = "https://www.pangle.cn/union_pangle/api/report/list";  //查询报表接口url
    @Resource
    private ThirdPlatApiService thirdPlatApiService;
    @Value("${ding.robot.third-import.secret}")
    private String dingSecret;
    @Value("${ding.robot.third-import.accessToken}")
    private String dingAccessToken;
    @Value("${spring.profiles.active}")
    private String active;
    @Resource
    private UserService userService;

    /**
     * 请求穿山甲接口
     * @param startDate 开始日期，格式：yyyy-MM-dd
     * @param endDate 结束日期, 格式：yyyy-MM-dd
     * @return
     */
    public JSONArray getReportDatas(String startDate, String endDate, String username, String appid, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("cookie", cookie);  //请求头中添加cookie
        String params = this.paramsJson(startDate, endDate);

        HttpEntity<String> request = new HttpEntity<String>(params, headers);
        String result = restTemplate.postForObject(reportUrl, request, String.class);
        if(result.indexOf("PG0001") >= 0 && "prod".equals(active)) {
            //cookie已经过期
            String content = "穿山甲账号的cookie已过期，请在“轩辕系统--广告平台管理”模块的Secure Key上重新设置cookie（用户账号：%s，appid：%s）";
            content = String.format(content, username, appid);
            log.error(content);
            User user = userService.queryUserByUsername(username);
            if(user != null && user.getUserType() == 2) {
                RobotUtil.talkText(content,dingSecret,dingAccessToken);  //调用钉钉机器人，往群里发送消息
            }
        }
        return JSONObject.parseObject(result).getJSONObject("Data").getJSONObject("ReportInfo").getJSONArray("ReportList");
    }

    /**
     * 获取穿山甲接口广告接口数据
     * @param date 日期，格式：yyyy-MM-dd
     * @param secret secret
     * @return
     */
    public List<ThirdPlatDataVo> getDataList(String date, String username, String appid, String secret) {
        List<ThirdPlatDataVo> list = new ArrayList<>();
        try {
            JSONArray array = this.getReportDatas(date, date, username, appid, secret);
            for(int i=0;i<array.size();i++) {
                list.add(this.formatData(array.getJSONObject(i)));
            }
            if(!list.isEmpty()) {
                //第三方平台的广告数据导入成功
                this.thirdPlatApiService.setSuccessTag(date, appid, secret);
            }
            return list;
        } catch (Exception e) {
            log.error("穿山甲获取数据失败, cookie:{}", secret);
            log.error("穿山甲第三方数据失败", e);
            return list;
        }
    }

    private ThirdPlatDataVo formatData(JSONObject json) {
        ThirdPlatDataVo dataVo = new ThirdPlatDataVo();
        dataVo.setDate(json.getString("StartDate"));
        dataVo.setAdSource(ThirdPlatEnum.CHUANG_SHAN_JIA.getCode());  //平台key
        dataVo.setAppId(json.getString("SiteId"));  //应用id
        dataVo.setAppName(json.getString("SiteName"));  //应用名称
        dataVo.setAdId(json.getString("CodeId"));  //代码位ID
        dataVo.setAdName(json.getString("CodeName"));  //代码位名称
        dataVo.setAdType(json.getString("CodeType"));  //代码位类型(1：信息流广告,2：Banner广告,3：开屏广告,4：插屏广告,5：激励视频广告,6：全屏视频广告,7：Draw信息流广告)
        dataVo.setShow(json.getInteger("IpmCnt"));  //展示量
        dataVo.setClick(json.getInteger("ClkCnt"));  //点击量
        dataVo.setRevenue(json.getDouble("Revenue"));  //收益
        dataVo.setEcpm(json.getDouble("Ecpm"));  //ecpm
        return dataVo;
    }

    private String paramsJson(String startDate, String endDate) {
        StringBuffer param = new StringBuffer();
        param.append("{");
        param.append("  'Filters': {");
        param.append("    'StartDate': {");
        param.append("      'Value': ['%s']");
        param.append("    },");
        param.append("    'EndDate': {");
        param.append("      'Value': ['%s']");
        param.append("    },");
        param.append("    'TimeZone': {");
        param.append("      'Value': ['8']");
        param.append("    },");
        param.append("    'DateType': {");
        param.append("      'Value': ['day']");
        param.append("    }");
        param.append("  },");
        param.append("  'Dimensions': ['CodeIds'],");
        param.append("  'Pagination': {");
        param.append("    'Page': 1,");
        param.append("    'PageSize': 10000");
        param.append("  }");
        param.append("}");
        String jsonParam = param.toString();
        jsonParam = String.format(jsonParam, startDate, endDate);
        jsonParam = jsonParam.replace("'", "\"");
        return jsonParam;
    }
}
