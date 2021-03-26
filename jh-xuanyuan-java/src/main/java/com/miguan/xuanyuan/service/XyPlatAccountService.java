package com.miguan.xuanyuan.service;


import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.dto.PlatAccountDto;
import com.miguan.xuanyuan.dto.PlatAccountListDto;
import com.miguan.xuanyuan.entity.XyPlatAccount;

import javax.validation.groups.Default;
import java.util.List;

public interface XyPlatAccountService extends Default{

    XyPlatAccount getDataById(Long id);

    List<XyPlatAccount> getUserPlatAccountList(Long userId);

    /**
     * 广告平台账号列表
     * @param username 用户账号
     * @param userId 用户id
     * @return
     */
    PageInfo<PlatAccountListDto> findAccountList(String username, Long userId, Integer pageNum, Integer pageSize);

    /**
     * 保存平台账号（根据id进行新增或修改操作）
     * @param accountDto
     */
    void savePlatAccount(PlatAccountDto accountDto);

    /**
     * 根据id查询平台账号信息
     * @param id
     * @return
     */
    PlatAccountDto getPlatAccount(Long id);

    /**
     * 每个账号注册后，初始化【穿山甲】【广点通】【快手】三个平台，仅做账号管理功能
     * @param userId 用户id
     */
    void insertDefaultPlatAccount(Integer userId);
}
