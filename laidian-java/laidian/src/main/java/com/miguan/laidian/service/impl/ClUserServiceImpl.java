package com.miguan.laidian.service.impl;


import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.LaidianUtils;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.mapper.ClUserMapper;
import com.miguan.laidian.redis.service.RedisClient;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.ClSmsService;
import com.miguan.laidian.service.ClUserService;
import com.miguan.laidian.vo.ClUserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用户表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

@Service("clUserService")
public class ClUserServiceImpl implements ClUserService {

    @Resource
    private ClUserMapper clUserMapper;

    @Autowired
    private ClSmsService clSmsService;

    @Resource
    private RedisClient redisClient;

    /**
     * 用户登录
     *
     * @param appType   马甲包类型
     * @param clUserVo  用户信息
     * @param vcode     短信验证码
     * @return
     */
    public Map<String, Object> login(String appType, ClUserVo clUserVo, String vcode) {
        Map<String, Object> result = new HashMap<String, Object>();
        String loginName = clUserVo.getLoginName();
        int results = clSmsService.verifySms(loginName, "login", vcode, appType);
        if (results != 1) {
            String vmsg = results == -1 ? "验证码已过期" : "验证码错误";
            result.put("success", "-1");
            result.put("msg", vmsg);
            return result;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginName", loginName);
        List<ClUserVo> clUserList = clUserMapper.findClUserList(paramMap);
        if (clUserList == null || clUserList.size() == 0) {
            result.put("register", "1");//注册标识
            String suffixName = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            clUserVo.setName("xy_" + suffixName);
            String appEnvironment = Global.getValue("app_environment", appType);
            if ("prod".equals(appEnvironment)) {
                clUserVo.setImgUrl(Global.getValue("prod_default_head_img", appType));//生产默认头像
            } else {
                clUserVo.setImgUrl(Global.getValue("dev_default_head_img", appType));//测试默认头像
            }
            clUserVo.setState(ClUserVo.USE);//状态 10正常 20禁用
            clUserMapper.saveClUser(clUserVo);
        } else {
            //用户被禁用状态
            if (ClUserVo.NO_USE.equals(clUserList.get(0).getState())) {
                result.put("success", "-1");
                result.put("msg", "该用户被禁用");
                return result;
            }
            //更新登陆时间
            clUserMapper.updateClUser(clUserVo);
        }
        clUserList = clUserMapper.findClUserList(paramMap);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        result.put("token", token);
        result.put("userId", String.valueOf(clUserList.get(0).getId()));

        redisClient.set(RedisKeyConstant.USER_TOKEN + clUserList.get(0).getId(), token, RedisKeyConstant.USER_TOKEN_SECONDS);  //token失效时间30天

        result.put("success", "0");
        result.put("msg", "登录成功");
        result.put("userInfo", clUserList.get(0));
        return result;
    }

    @Override
    public List<ClUserVo> findClUserList(Map<String, Object> params) {
        return clUserMapper.findClUserList(params);
    }

    @Override
    public int saveClUser(ClUserVo clUserVo) {
        return clUserMapper.saveClUser(clUserVo);
    }

    @Override
    public int updateClUser(ClUserVo clUserVo) {
        return clUserMapper.updateClUser(clUserVo);
    }

    @Override
    public List<ClUserVo> findAllTokens() {
        return clUserMapper.findAllTokens();
    }

}