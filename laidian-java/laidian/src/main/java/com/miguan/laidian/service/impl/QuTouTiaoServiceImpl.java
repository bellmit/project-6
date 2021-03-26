package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.util.HttpUtils;
import com.miguan.laidian.entity.QuHeadlinesCustomer;
import com.miguan.laidian.mapper.QuHeadlinesCustomerMapper;
import com.miguan.laidian.service.QuTouTiaoService;
import com.miguan.laidian.vo.CheckQuTouTiaoVo;
import com.miguan.laidian.vo.SaveQuTouTiaoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class QuTouTiaoServiceImpl implements QuTouTiaoService {

    @Resource
    private QuHeadlinesCustomerMapper quHeadlinesCustomerMapper;

    @Override
    public Map<String, Object> insertSelective(SaveQuTouTiaoVo params) {
        QuHeadlinesCustomer quHeadlinesCustomer = new QuHeadlinesCustomer();
        BeanUtils.copyProperties(params,quHeadlinesCustomer);
        try {
            String utf8 = URLDecoder.decode(params.getCallback_url(), "UTF8");
            quHeadlinesCustomer.setCallbackUrl(utf8);
            quHeadlinesCustomer.setCreateTime(new Date());
        }catch (Exception e){
            log.info("转码异常................",e.getMessage());
        }
        HashMap<String,Object> result = new HashMap<>();
        try {
            quHeadlinesCustomerMapper.insertSelective(quHeadlinesCustomer);
        } catch (Exception e) {
            result.put("status",500);
            log.error("保存趣头条用户相关信息为失败................",e);
            return result;
        }
        result.put("status",0);
        log.info("保存趣头条用相关信息数据成功");
        return result;
    }

    @Override
    public Map<String, Object> selectByImeiAndAndroidid(CheckQuTouTiaoVo params) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("success",false);
        QuHeadlinesCustomer quHeadlinesCustomer = quHeadlinesCustomerMapper.selectByImeiAndAndroidid(params);
        if(quHeadlinesCustomer != null){
            String content = HttpUtils.doGet(quHeadlinesCustomer.getCallbackUrl()+"&op2="+params.getOp2(),null,new HashMap<>());
            log.info("获得的返回结果=>{}", content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            if(jsonObject.getInteger("code") != null){
                Integer code = jsonObject.getInteger("code");
                if(code == 0){
                    result.put("success",true);
                    return result;
                }
            }
        }
        return result;
    }
}
