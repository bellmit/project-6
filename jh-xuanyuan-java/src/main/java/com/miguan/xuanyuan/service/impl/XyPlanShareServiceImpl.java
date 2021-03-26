package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.util.AESUtils;
import com.miguan.xuanyuan.common.util.DateUtils;
import com.miguan.xuanyuan.entity.XyPlanPositionCode;
import com.miguan.xuanyuan.entity.XyPlanShare;
import com.miguan.xuanyuan.mapper.XyPlanShareMapper;
import com.miguan.xuanyuan.mapper.XyUserExtMapper;
import com.miguan.xuanyuan.service.XyPlanShareService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 广告计划分享表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
@Service
public class XyPlanShareServiceImpl extends ServiceImpl<XyPlanShareMapper, XyPlanShare> implements XyPlanShareService {

    public String generateShareCode(Long planId, Long userId) throws Exception {
        XyPlanShareMapper mapper = this.getBaseMapper();
        LocalDateTime expiredDate = DateUtils.getDate(XyConstant.PLAN_REPORT_SHARE_EXPIRED);

        XyPlanShare entity = new XyPlanShare();
        entity.setPlanId(planId);
        entity.setOperateUserId(userId);
        entity.setExpiredAt(expiredDate);
        mapper.insert(entity);
        String code = AESUtils.encrypt(String.valueOf(entity.getId()));
        return code;
    }
}
