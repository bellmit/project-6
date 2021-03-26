package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.miguan.reportview.entity.AdAdvertCode;
import com.miguan.reportview.entity.AdAdvertPosition;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.mapper.AdAdvertCodeMapper;
import com.miguan.reportview.mapper.AdAdvertPositionMapper;
import com.miguan.reportview.service.IAdAdvertService;
import com.miguan.reportview.vo.AdSpaceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 广告库 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Service
@DS("ad-db")
@Slf4j
public class AdAdvertServiceImpl implements IAdAdvertService {
    private Map<String, List<AdSpaceVo>> cacheMap;
    @Resource
    private AdAdvertPositionMapper adAdvertPositionMapper;
    @Resource
    private AdAdvertCodeMapper adAdvertCodeMapper;
    @Autowired
    private AppServiceImpl appService;
    private Object lock = new Object();

    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(cacheMap)) {
            synchronized (lock) {
                if (CollectionUtils.isEmpty(cacheMap)) {
                    try {
                        DynamicDataSourceContextHolder.push("ad-db");
                        List<AdSpaceVo> spaces = getAdSpaceByApp(null,null);
                        cacheMap = spaces.stream().collect(Collectors.groupingBy(AdSpaceVo::getKey));
                        spaces.clear();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        DynamicDataSourceContextHolder.poll();
                    }
                }
            }
        }
    }

    @Override
    public void sysAdSpace() {
        cacheMap = null;
        init();
    }

    @Override
    public List<AdSpaceVo> getAdSpaceByApp(Integer appType, String... appPackage) {
        LambdaQueryWrapper<AdAdvertPosition> wrap = Wrappers.<AdAdvertPosition>lambdaQuery()
                .select(AdAdvertPosition::getAppPackage, AdAdvertPosition::getReportShowName, AdAdvertPosition::getPositionType)
                .isNotNull(AdAdvertPosition::getReportShowName);
        if (Objects.nonNull(appPackage)) {
            wrap.in(AdAdvertPosition::getAppPackage, appPackage);
        } else if (!CollectionUtils.isEmpty(cacheMap)) {
            List<AdSpaceVo> cacheList = cacheMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
            filterXld(appType, cacheList);
            return cacheList;
        }
        List<AdAdvertPosition> allAd = adAdvertPositionMapper.selectList(wrap);
        List<AdSpaceVo> rdata = allAd.stream().map(e -> {
            if("xld".equals(e.getAppPackage())) {
                e.setAppPackage("com.mg.phonecall");
            }
            AdSpaceVo vo = new AdSpaceVo();
            vo.setAppPackage(e.getAppPackage());
            vo.setName(e.getReportShowName());
            App app = appService.getAppByPackageName(e.getAppPackage());
            vo.setAppName(app != null ? app.getName() : "");
            vo.setKey(e.getPositionType());
            return vo;
        }).collect(Collectors.toList());
        return rdata;
    }

    private void filterXld(Integer appType, List<AdSpaceVo> list) {
        if(appType == null) {
            return;
        }
        Iterator it = list.iterator();
        while(it.hasNext()) {
            AdSpaceVo adSpaceVo = (AdSpaceVo)it.next();
            if(appType == 1 && "com.mg.phonecall".equals(adSpaceVo.getAppPackage())) {
                //如果是视频广告位的话，删除来电的广告位
                it.remove();
            }
            if(appType == 2 && !"com.mg.phonecall".equals(adSpaceVo.getAppPackage())) {
                //如果查询来电广告位的话，删除视频的广告位
                it.remove();
            }
        }
    }

    @Override
    public List<String> findAdCodeForLike(String likeCode) {
        if (StringUtils.isBlank(likeCode)) {
            throw new NullPointerException("请求参数不能为空 likeCode");
        }
        QueryWrapper<AdAdvertCode> wrapper = Wrappers.<AdAdvertCode>query()
                .select("DISTINCT ad_id").likeRight("ad_id", likeCode);
        return adAdvertCodeMapper.selectObjs(wrapper).stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public AdSpaceVo getAdspaceForName(String name) {

        return null;
    }

    @Override
    public List<AdAdvertPosition> getAll() {
        return adAdvertPositionMapper.selectList(Wrappers.<AdAdvertPosition>emptyWrapper());
    }

    @Override
    public String getAdSpaceNname(String key) {
        if (cacheMap == null || key == null || !cacheMap.containsKey(key)) {
            return key;
        }
        return cacheMap.get(key).get(0).getName();
    }
}
