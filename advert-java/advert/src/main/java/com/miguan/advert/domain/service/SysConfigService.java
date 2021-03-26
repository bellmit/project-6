package com.miguan.advert.domain.service;

import com.github.pagehelper.PageInfo;
import com.miguan.advert.domain.dto.SysConfigDto;

/**
 * @Description 开关配置service
 * @Author zhangbinglin
 * @Date 2020/11/11 11:06
 **/
public interface SysConfigService {

    /**
     * 查询开关配置列表
     * @param type 类型
     * @param name 参数名称
     * @param status 状态
     * @return
     */
    PageInfo<SysConfigDto> listSysConfig(Integer type, String name, Integer status, int pageNum, int pageSize);

    /**
     * 根据id查询配置信息
     * @param id 主键id
     * @return
     */
    SysConfigDto getSysConfig(Long id);

    /**
     * 新增或修改 配置
     * @param dto
     */
    void saveSysConfig(SysConfigDto dto);

    /**
     * 删除配置
     * @param code 配置编号
     */
    void deleteSysConfig(String code);
}
