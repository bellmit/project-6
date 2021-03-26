package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.entity.XyUserExt;
import com.miguan.xuanyuan.mapper.XyUserExtMapper;
import com.miguan.xuanyuan.service.XyUserExtService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * <p>
 * 用户扩展表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
@Service
public class XyUserExtServiceImpl extends ServiceImpl<XyUserExtMapper, XyUserExt> implements XyUserExtService {


    public void setShareSwitch(long userId, int shareSwitch) throws ServiceException {
        if (shareSwitch != XyConstant.PLAN_SHARE_SWITCH_OPEN && shareSwitch != XyConstant.PLAN_SHARE_SWITCH_CLOSE) {
            throw new ServiceException("参数shareSwitch错误");
        }
        XyUserExtMapper mapper = this.getBaseMapper();
        XyUserExt userExt = this.getUserExtByUserId(userId);
        if (userExt == null) {
            userExt = new XyUserExt();
            userExt.setPlanReportShare(shareSwitch);
            userExt.setUserId(userId);
            mapper.insert(userExt);
        } else {
            userExt.setPlanReportShare(shareSwitch);
            mapper.updateById(userExt);
        }
    }

    /**
     * 根据用户id获取扩展信息
     *
     * @param userId
     * @return
     */
    public XyUserExt getUserExtByUserId(Long userId) {
        XyUserExtMapper mapper = this.getBaseMapper();
        return mapper.getUserExtByUserId(userId);
    }

    /**
     * 获取用户分享开关
     *
     * @param userId
     * @return
     */
    public Integer getUserShareSwitch(Long userId) {
        XyUserExt userExtInfo =  this.getUserExtByUserId(userId);
        Integer shareSwitch = XyConstant.PLAN_SHARE_SWITCH_OPEN;
        if (userExtInfo != null) {
            shareSwitch = userExtInfo.getPlanReportShare();
        }
        return shareSwitch;
    }

}
