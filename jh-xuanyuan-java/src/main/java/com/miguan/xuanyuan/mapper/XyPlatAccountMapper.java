package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.miguan.xuanyuan.dto.PlatAccountListDto;
import com.miguan.xuanyuan.entity.XyPlatAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface XyPlatAccountMapper extends BaseMapper<XyPlatAccount> {

    /**
     * 广告平台账号列表
     * @return
     */
    Page<PlatAccountListDto> findAccountList(Map<String, Object> params);

    /**
     * 每个账号注册后，初始化【穿山甲】【广点通】【快手】三个平台，仅做账号管理功能
     * @param userId 用户id
     */
    void insertDefaultPlatAccount(@Param("userId") Integer userId);

    List<XyPlatAccount> findDefaultPlatAccount(@Param("userId") Integer userId);

    XyPlatAccount getDataById(Long id);

    List<XyPlatAccount> getUserPlatAccountList(Long userId);
}
