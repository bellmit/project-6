package com.miguan.idmapping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.idmapping.entity.UserGold;
import com.miguan.idmapping.vo.ClUserVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户金币信息表 服务类
 * </p>
 *
 * @author jobob
 * @since 2020-07-21
 */
public interface IUserGoldService extends IService<UserGold> {
    boolean initUserGold(Map<String, Object> result, ClUserVo clUserVo, List<ClUserVo> clUserList,String type);
}
