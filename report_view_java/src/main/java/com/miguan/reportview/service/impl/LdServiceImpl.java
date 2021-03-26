package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.miguan.reportview.entity.VideosCat;
import com.miguan.reportview.mapper.FirstVideosMapper;
import com.miguan.reportview.mapper.VideosCatMapper;
import com.miguan.reportview.service.ILdService;
import com.miguan.reportview.service.IVideosService;
import com.miguan.reportview.vo.FirstVideosStaVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 来电类别表 服务实现类
 * </p>
 *
 */
@Service
@DS("ld-db")
@Slf4j
public class LdServiceImpl implements ILdService {
    @Resource
    private VideosCatMapper videosCatMapper;
    private Map<Long, String> cacheCat;

    @PostConstruct
    public void init() {
        try {
            DynamicDataSourceContextHolder.push("ld-db");
            List<VideosCat> list = getVideosCat();
            cacheCat = list.stream().collect(Collectors.toMap(VideosCat::getId, VideosCat::getName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }

    }

    @Override
    public String getCatName(String catid) {
        long catidL = Long.valueOf(catid).longValue();
        if (cacheCat == null || !cacheCat.containsKey(catidL)) {
            return catid;
        }
        return cacheCat.get(catidL);
    }

    @Override
    public List<VideosCat> getVideosCat() {
        LambdaQueryWrapper<VideosCat> wrapper = Wrappers.<VideosCat>lambdaQuery().select(VideosCat::getId, VideosCat::getName, VideosCat::getType).orderByAsc(VideosCat::getSort);
        List<VideosCat> list = videosCatMapper.selectList(wrapper);
        return list;
    }

}
