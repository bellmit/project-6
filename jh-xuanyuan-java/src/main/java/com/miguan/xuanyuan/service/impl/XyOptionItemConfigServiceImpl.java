package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import com.miguan.xuanyuan.mapper.XyOptionItemConfigMapper;
import com.miguan.xuanyuan.service.XyOptionItemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author kangkunhuang
 * @Description 选项配置业务
 * @Date 2021/1/21
 **/
@Service
public class XyOptionItemConfigServiceImpl implements XyOptionItemConfigService {

    @Resource
    private XyOptionItemConfigMapper mapper;

    @Override
    @Transactional
    public void saveBatch(String configCode, List<XyOptionItemConfig> xyOptionItemConfig) throws ValidateException {
        //校验
        boolean b = judgeExistItem(xyOptionItemConfig);
        if(!b){
            throw new ValidateException("存在相同的参数key！");
        }
        //删除被删除的项
        List<Long> ids = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(xyOptionItemConfig)){
            ids = xyOptionItemConfig.stream().filter(config -> config.getId() != null).map(XyOptionItemConfig::getId).collect(Collectors.toList());
        }
        //查询要删除的id
//        List<Long> delIds = mapper.findDeleteIds(configCode,ids);
        mapper.deleteBatch(configCode,ids);
        //批量更新项
        if(CollectionUtils.isNotEmpty(xyOptionItemConfig)){
            mapper.saveBatch(xyOptionItemConfig);
        }
    }

    private boolean judgeExistItem(List<XyOptionItemConfig> configs) {
        if(CollectionUtils.isEmpty(configs)){
            return true;
        }
        //判断本身是否有重合key
        Set<String> keys = Sets.newHashSet();
        for (XyOptionItemConfig config : configs) {
            if(keys.contains(config.getItemKey())){
                return false;
            }
            keys.add(config.getItemKey());
        }
        return true;
    }

    @Override
    public List<XyOptionItemConfig> findByConfigCode(String configCode) {
        return mapper.findByConfigCode(configCode);
    }

    @Override
    public XyOptionItemConfig findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public XyOptionItemConfig findByCodeAndKey(String codeConfig, String itemKey) {
        return mapper.findByCodeAndKey(codeConfig, itemKey);
    }

    @Override
    public List<XyOptionItemConfig> findOperationPlatType(List<String> allConfigCode, String configCode) {
        return mapper.findOperationPlatType(allConfigCode, configCode);
    }
}
