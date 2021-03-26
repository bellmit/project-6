package com.miguan.xuanyuan.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.entity.XyMapConfig;
import com.miguan.xuanyuan.mapper.XyMapConfigMapper;
import com.miguan.xuanyuan.service.XyMapConfigService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.XyMapConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 参数配置业务
 * @Date 2021/1/21
 **/
@Service
public class XyMapConfigServiceImpl implements XyMapConfigService {

    @Resource
    private XyMapConfigMapper mapper;


    @Resource
    private RedisService redisService;

    @Override
    public void save(XyMapConfig xyMapConfig) throws ValidateException {
        int count = mapper.judgeExistCode(xyMapConfig.getConfigKey(),xyMapConfig.getId());
        if(count > 0){
            throw new ValidateException("该参数key已存在！");
        }
        if(xyMapConfig.getId() == null){
            mapper.insert(xyMapConfig);
        } else {
            mapper.update(xyMapConfig);
        }
        if(xyMapConfig.getStatus() != null && xyMapConfig.getConfigKey() != null && xyMapConfig.getConfigVal() != null) {
            if(xyMapConfig.getStatus() == 1) {
                //状态是启用状态的，添加redis缓存
                redisService.set(RedisKeyConstant.CONFIG_CODE + xyMapConfig.getConfigKey(), xyMapConfig.getConfigVal(), -1);
            } else {
                //状态是禁用状态的，删除redis缓存
                redisService.del(RedisKeyConstant.CONFIG_CODE + xyMapConfig.getConfigKey());
            }
        }
    }

    @Override
    public PageInfo<XyMapConfigVo> pageList(String keyword, Integer status, String sort, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("keyword",keyword);
        params.put("status",status);
        params.put("sort", StringUtils.isEmpty(sort) ? "id desc" : StringUtil.humpToLine(sort));
        PageHelper.startPage(pageNum, pageSize);
        Page<XyMapConfigVo> pageResult = mapper.findPageList(params);
        return new PageInfo(pageResult);
    }

    @Override
    public List<XyMapConfig> findOpenConfig() {
        return mapper.findOpenConfig();
    }

    @Override
    public XyMapConfig findById(Long id) {
        return mapper.findById(id);
    }
}
