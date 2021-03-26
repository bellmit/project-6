package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.miguan.xuanyuan.dto.IdentityListBackDto;
import com.miguan.xuanyuan.entity.Identity;

import java.util.Map;

/**
 * <p>
 * 账号认证表 Mapper 接口
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-27
 */
public interface IdentityMapper extends BaseMapper<Identity> {

    /**
     * 分页查询媒体账号列表
     * @param params
     * @return
     */
    Page<IdentityListBackDto> pageIdentityList(Map<String, Object> params);

    Identity getIdentityByUserId(Long userId);
}
