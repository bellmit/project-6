package com.miguan.recommend.service.xy.impl;

import com.cgcg.context.util.StringUtils;
import com.miguan.recommend.bo.CatWeightDto;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.mapper.ClInterestLabelMapper;
import com.miguan.recommend.service.xy.ClInterestLabelService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClInterestLabelServiceImpl implements ClInterestLabelService {

    @Resource
    private ClInterestLabelMapper clInterestLabelMapper;

    @Override
    @Cacheable(cacheNames = "user_choose_cat", cacheManager = "getCacheManager")
    public List<Integer> findCatIdOfUserChoose(String uuid) {
        // 如果库中不存在，说明
        String catids = clInterestLabelMapper.getCatIdsOfUserChoose(uuid);
        if(StringUtils.isEmpty(catids)){
            return null;
        }
        return Stream.of(catids.split(SymbolConstants.comma)).map(Integer::new).collect(Collectors.toList());
    }
}
