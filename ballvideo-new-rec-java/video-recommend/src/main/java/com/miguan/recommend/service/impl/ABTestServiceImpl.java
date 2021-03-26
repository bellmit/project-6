package com.miguan.recommend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.HttpUtil;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.service.ABTestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.cgcg.context.util.HttpUtil.APPLICATION_FOEM_URLENCODED;

@Slf4j
@Service
@Component
public class ABTestServiceImpl implements ABTestService {

    @Value("${spring.abtest.uri}")
    private String abtestUri;

    /**
     * 获取实验信息
     *
     * @param expCode      实验标识
     * @param variableName 变量名
     * @return
     */
    @Override
    @Cacheable(cacheNames = "default", cacheManager = "getCacheManager")
    public Map<String, String> getABTestGroupInfoByExpKey(String expCode, String variableName) {
        Map<String, String> groupInfoMap = new HashMap<String, String>();
        String responseStr = HttpUtil.post(abtestUri, "exp_key=" + expCode, "UTF-8", APPLICATION_FOEM_URLENCODED);
        JSONObject response = JSONObject.parseObject(responseStr);
        int code = response.getIntValue("code");
        if (code == 200) {
            // 获取返回数据
            JSONObject data = response.getJSONObject("data");
            // 获取实验信息
            JSONObject expInfo = data.getJSONObject("exp_info");
            // 获取实验ID
            int expId = expInfo.getIntValue("id");
            // 获取实验组信息(数组)
            JSONArray groups = data.getJSONArray("exp_group");
            // 遍历实验组
            for (int i = 0; i < groups.size(); i++) {
                JSONObject group = groups.getJSONObject(i);
                // 获取实验组ID
                int groupId = group.getIntValue("id");
                // 解析实验组对应的java_type
                String vars = group.getString("vars");
                if (StringUtils.isNotEmpty(vars)) {
                    String name = group.getJSONObject("vars").getString(variableName);
                    String abAliases = null;
                    if (groupInfoMap.containsKey(name)) {
                        abAliases = groupInfoMap.get(name);
                        abAliases = abAliases + SymbolConstants.comma + expId + SymbolConstants.line + groupId;
                    } else {
                        abAliases = expId + SymbolConstants.line + groupId;
                    }
                    log.debug("AB实验平台,[{}]实验,[{}]组,标识[{}]", expCode, name, abAliases);
                    groupInfoMap.put(name, abAliases);
                } else {
                    log.warn("AB实验平台,[{}]实验,[{}]组无{}标识", expCode, groupId, variableName);
                }
            }
        } else {
            log.warn("AB实验平台,[{}]实验信息返回异常>>{}", expCode, response.toJSONString());
        }
        log.debug("AB实验平台,[{}]实验信息", expCode, groupInfoMap);
        return groupInfoMap;
    }
}
