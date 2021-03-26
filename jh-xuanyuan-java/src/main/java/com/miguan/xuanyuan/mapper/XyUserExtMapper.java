package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.XyUserExt;

/**
 * <p>
 * 用户扩展表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-16
 */
public interface XyUserExtMapper extends BaseMapper<XyUserExt> {

    XyUserExt getUserExtByUserId(Long userId);

}
