package com.miguan.xuanyuan.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.entity.XyOptionConfig;
import com.miguan.xuanyuan.mapper.XyOptionConfigMapper;
import com.miguan.xuanyuan.service.XyOptionConfigService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.XyOptionConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 选项配置业务
 * @Date 2021/1/21
 **/
@Service
public class XyOptionConfigServiceImpl implements XyOptionConfigService {

    @Resource
    private XyOptionConfigMapper mapper;


    @Resource
    private RedisService redisService;

    @Override
    public void save(XyOptionConfig xyOptionConfig) throws ValidateException {
        if(xyOptionConfig.getId() == null){
            if(judgeExistCode(xyOptionConfig.getConfigCode())){
                throw new ValidateException("该英文字段已存在！");
            }
            mapper.insert(xyOptionConfig);
        } else {
            mapper.update(xyOptionConfig);
        }
    }

    @Override
    public boolean judgeExistCode(String code) {
        return mapper.judgeExistCode(code) > 0 ? true : false;
    }


    @Override
    public PageInfo<XyOptionConfigVo> pageList(String keyword, Integer status, String sort, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("keyword",keyword);
        params.put("status",status);
        params.put("sort", StringUtils.isEmpty(sort) ? "id desc" : StringUtil.humpToLine(sort));
        PageHelper.startPage(pageNum, pageSize);
        Page<XyOptionConfigVo> pageResult = mapper.findPageList(params);
        return new PageInfo(pageResult);
    }

    @Override
    public XyOptionConfig findById(Long id) {
        return mapper.findById(id);
    }
}
