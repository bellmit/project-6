package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.dao.DspAdvUserDao;
import com.miguan.ballvideo.entity.dsp.IdeaAdvertUserVo;
import com.miguan.ballvideo.service.dsp.DspAdvUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dsp广告主管理 Service
 * @author suhongju
 * @date 2020-09-22
 */
@Slf4j
@Service
public class DspAdvUserServiceImpl implements DspAdvUserService {

    @Autowired
    public DspAdvUserDao dspAdvUserDao;

    @Override
    public List<Map<String, Object>> getList(String name, String type, String adUserId) {
        List<Map<String, Object>> advLst = dspAdvUserDao.getList(name, type, adUserId);
        if(CollectionUtils.isEmpty(advLst)){
            return null;
        }
        advLst.forEach(adv -> {
            List<Map<String, Object>> planLst = dspAdvUserDao.findPlanLst(""+ adv.get("id"));
            if(CollectionUtils.isNotEmpty(planLst)){
                List<String> idLst = planLst.stream().map(plan -> ""+plan.get("id")).collect(Collectors.toList());
                adv.put("plan_ids", String.join(",",idLst));
            }
        });
        return advLst;
    }

    @Override
    public Map<String, Object> getInfo(String adUserId) {
        List<Map<String, Object>> advLst = dspAdvUserDao.getList(null, null, adUserId);
        return advLst.get(0);
    }

    @Override
    public String advAddEdit(IdeaAdvertUserVo ideaAdvertUserVo) {
        String adUserId = ideaAdvertUserVo.getAd_user_id();
        if(StringUtil.isEmpty(ideaAdvertUserVo.getAd_user_id())){
            adUserId = dspAdvUserDao.save(ideaAdvertUserVo);
        }else{
            dspAdvUserDao.updateById(ideaAdvertUserVo);
        }
        return adUserId;
    }



}
