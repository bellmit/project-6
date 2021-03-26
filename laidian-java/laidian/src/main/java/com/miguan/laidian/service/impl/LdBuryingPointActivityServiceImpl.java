package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;

import com.miguan.laidian.mapper.BuryingMapper;
import com.miguan.laidian.service.LdBuryingPointActivityService;
import com.miguan.laidian.vo.LdBuryingPointActivityVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenwf
 * @date 2020/5/27
 */
@Service
@Transactional
public class LdBuryingPointActivityServiceImpl implements LdBuryingPointActivityService {

    @Resource
    private BuryingMapper buryingMapper;

    /**
     * 活动埋点写入
     *
     * @param ldBuryingPointActivityVo
     */
    @Override
    public void insert(LdBuryingPointActivityVo ldBuryingPointActivityVo) {
        final Map<String, Object> datas = new ConcurrentHashMap<>(100);
        String jsonStr = JSONObject.toJSONString(ldBuryingPointActivityVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.put("createDate",ldBuryingPointActivityVo.getCreateDate());
        jsonMap.keySet().forEach(e -> datas.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        buryingMapper.insertDynamic("ld_burying_point_activity",datas);
    }
}
