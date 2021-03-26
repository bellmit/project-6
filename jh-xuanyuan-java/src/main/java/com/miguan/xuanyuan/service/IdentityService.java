package com.miguan.xuanyuan.service;

import com.github.pagehelper.PageInfo;
import com.miguan.xuanyuan.dto.IdentityBackDto;
import com.miguan.xuanyuan.dto.IdentityListBackDto;
import com.miguan.xuanyuan.entity.Identity;

/**
 * <p>
 * 账号认证表 服务类
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-27
 */
public interface IdentityService {

    /**
     * 分页查询媒体账号列表
     * @param phone  用户注册电话
     * @param name  企业/个人名称
     * @param status  状态
     * @param pageNum  分页页码
     * @param pageSize 分页每页记录数
     * @return
     */
    PageInfo<IdentityListBackDto> pageIdentityList(String phone, String name, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 保存媒体账号信息（根据id进行新增或修改操作）
     * @param identity
     */
    void saveIdentity(Identity identity);

    /**
     * 后台保存媒体账号信息
     * @param identityBackDto
     */
    void saveBackIdentity(IdentityBackDto identityBackDto);

    /**
     * 根据id查询媒体账号信息
     * @param id
     * @return
     */
    Identity getIdentity(Integer id);

    /**
     * 根据userId查询媒体信息
     * @param userId
     * @return
     */
    Identity getIdentityByUserId(Integer userId);

    /**
     * 获取后台媒体账号信息
     * @param id
     * @return
     */
    IdentityBackDto getBackIdentity(Integer identityId, Integer id);


    Identity getIdentityByUserId(Long userId);
}
