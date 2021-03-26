package com.miguan.xuanyuan.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.dto.PlatAccountDto;
import com.miguan.xuanyuan.dto.PlatAccountListDto;
import com.miguan.xuanyuan.entity.XyPlatAccount;
import com.miguan.xuanyuan.mapper.XyPlatAccountMapper;
import com.miguan.xuanyuan.service.XyPlatAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class XyPlatAccountServiceImpl implements XyPlatAccountService {

    @Resource
    public XyPlatAccountMapper xyPlatAccountMapper;

    /**
     * 广告平台账号列表
     * @param username 用户账号
     * @param userId 用户id
     * @return
     */
    public PageInfo<PlatAccountListDto> findAccountList(String username, Long userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("username", username);
        params.put("userId", userId);
        PageHelper.startPage(pageNum, pageSize);
        Page<PlatAccountListDto> pageResult = xyPlatAccountMapper.findAccountList(params);
        return new PageInfo(pageResult);
    }

    /**
     * 保存平台账号（根据id进行新增或修改操作）
     * @param accountDto
     */
    public void savePlatAccount(PlatAccountDto accountDto) {
        XyPlatAccount platAccount = new XyPlatAccount();
        BeanUtils.copyProperties(accountDto, platAccount);
        if(accountDto.getId() == null) {
            //新增操作
            xyPlatAccountMapper.insert(platAccount);
        } else {
            //修改操作
            xyPlatAccountMapper.updateById(platAccount);
        }
    }

    /**
     * 根据id查询平台账号信息
     * @param id
     * @return
     */
    public PlatAccountDto getPlatAccount(Long id) {
        XyPlatAccount platAccount = xyPlatAccountMapper.selectById(id);
        PlatAccountDto platAccountDto = new PlatAccountDto();
        BeanUtils.copyProperties(platAccount, platAccountDto);
        return platAccountDto;
    }

    public XyPlatAccount getDataById(Long id) {
        return xyPlatAccountMapper.getDataById(id);
    }

    public List<XyPlatAccount> getUserPlatAccountList(Long userId) {
        return xyPlatAccountMapper.getUserPlatAccountList(userId);
    }

    /**
     * 每个账号注册后，初始化【穿山甲】【广点通】【快手】三个平台，仅做账号管理功能
     * @param userId 用户id
     */
    public void insertDefaultPlatAccount(Integer userId) {
        xyPlatAccountMapper.insertDefaultPlatAccount(userId);
    }
}
