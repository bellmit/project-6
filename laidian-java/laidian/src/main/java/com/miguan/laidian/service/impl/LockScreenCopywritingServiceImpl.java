package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.mapper.LockScreenCopywritingMapper;
import com.miguan.laidian.service.LockScreenCopywritingService;
import com.miguan.laidian.vo.LockScreenCopywritingVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shixh
 * @Date 2019/12/11
 **/
@Slf4j
@Service
public class LockScreenCopywritingServiceImpl implements LockScreenCopywritingService {

    @Resource
    private LockScreenCopywritingMapper lockScreenCopywritingMapper;

    @Override
    public LockScreenCopywritingVo getCopywritingInfo(Map<String, Object> params) {
        final String excludeIds = MapUtils.getString(params, "excludeIds");
        final List<String> list = new ArrayList();
        if (!StringUtils.isEmpty(excludeIds)) {
            String[] split = excludeIds.split(",");
            list.addAll(Arrays.asList(split));
            params.put("excludeIds", list);
        } else {
            params.remove("excludeIds");
        }
        String today = DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd");
        params.put("effectiveDate", today);
        List<LockScreenCopywritingVo> copywritingVoList = lockScreenCopywritingMapper.queryCopywritingInfo(params);
        if (copywritingVoList.isEmpty()) {
            return null;
        }
        Map<String, List<LockScreenCopywritingVo>> copywritingVoMap = copywritingVoList.stream().collect(Collectors.groupingBy(LockScreenCopywritingVo::getEffectiveDate));
        //随机获取一条当日文案，若当日无文案，则随机获取一条“无限制”的文案
        LockScreenCopywritingVo lockScreenCopywritingVo = null;
        for (String effectiveDate : copywritingVoMap.keySet()) {
            if (today.equals(effectiveDate)) {
                if (copywritingVoMap.get(effectiveDate).size() > 1) {
                    Collections.shuffle(copywritingVoMap.get(effectiveDate));
                }
                return copywritingVoMap.get(effectiveDate).get(0);
            } else {
                if (copywritingVoMap.get(effectiveDate).size() > 1) {
                    Collections.shuffle(copywritingVoMap.get(effectiveDate));
                }
                lockScreenCopywritingVo = copywritingVoMap.get(effectiveDate).get(0);
            }
        }
        return lockScreenCopywritingVo;
    }

    @Override
    public boolean updateCopywritingClickNum(Long id) {
        boolean flag = false;
        try {
            int num = lockScreenCopywritingMapper.updateClickNum(id);
            if (num == 1) {
                flag = true;
            }
        } catch (Exception e) {
            log.info("更新锁屏文案次数失败,[{}]" + e.getMessage());
        }
        return flag;
    }
}
