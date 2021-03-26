package com.miguan.idmapping.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.idmapping.common.constants.RedisKeyConstant;
import com.miguan.idmapping.entity.UserGold;
import com.miguan.idmapping.mapper.UserGoldMapper;
import com.miguan.idmapping.service.IUserGoldService;
import com.miguan.idmapping.service.RedisDB2Service;
import com.miguan.idmapping.vo.ClUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户金币信息表 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2020-07-21
 */
@Service
@DS("task-db")
public class UserGoldServiceImpl extends ServiceImpl<UserGoldMapper, UserGold> implements IUserGoldService {

    @Autowired
    private RedisDB2Service redisDB2Service;

    /**
     * 初始化任务系统金币信息
     * @param clUserVo
     * @param clUserList
     */
    @Override
    public boolean initUserGold(Map<String, Object> result, ClUserVo clUserVo, List<ClUserVo> clUserList,String type) {
        try {
            String key = RedisKeyConstant.APP_TYPE + clUserVo.getAppPackage();
            String appType = redisDB2Service.get(key);
            int count = this.count(Wrappers.<UserGold>query().eq("user_id", clUserList.get(0).getId())
                    .eq("app_type", appType));
            if (count == 0) {
                UserGold userGold = new UserGold();
                userGold.setUserId(clUserList.get(0).getId());
                //1：微信、2：手机号；3：苹果手机
                if ("1".equals(type)){
                    userGold.setOpenId(clUserVo.getOpenId());
                }else if("2".equals(type)){
                    userGold.setPhone(clUserVo.getLoginName());
                }else if ("3".equals(type)){
                    userGold.setAppleId(clUserVo.getAppleId());
                }
                userGold.setTotalGold(0);
                userGold.setUsedGold(0);
                userGold.setCreatedAt(LocalDateTime.now());
                userGold.setFirstGold(0);
                userGold.setSecondGold(0);
                userGold.setThirdGold(0);
                userGold.setFourthGold(0);
                userGold.setAppType(appType);
                userGold.setAppPackage(clUserVo.getAppPackage());
                this.save(userGold);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化任务系统金币信息失败：userId:" + clUserList.get(0).getId() + "," + e.getMessage());
            result.put("success", "-1");
            result.put("msg", "网络异常，请重试");
            return false;
        }
        return true;
    }
}
