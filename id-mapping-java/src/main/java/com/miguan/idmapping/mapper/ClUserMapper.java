package com.miguan.idmapping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.idmapping.entity.ClUser;
import com.miguan.idmapping.vo.ClUserVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
public interface ClUserMapper extends BaseMapper<ClUser> {
    /**
     *
     * 通过条件查询用户列表
     *
     **/
    List<ClUserVo> findClUserList(Map<String, Object> params);

    /**
     *
     * 新增用户信息
     *
     **/
    int saveClUser(ClUserVo clUserVo);

    /**
     *
     * 修改用户信息
     *
     **/
    int updateClUser(ClUserVo clUserVo);
}
