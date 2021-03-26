package com.miguan.advert.domain.serviceimpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.advert.common.constants.Constant;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.domain.dto.SysConfigDto;
import com.miguan.advert.domain.mapper.SysConfigMapper;
import com.miguan.advert.domain.service.SysConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 开关配置service
 * @Author zhangbinglin
 * @Date 2020/11/11 11:07
 **/
@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;
    @Resource
    private RedisService redisService;

    /**
     * 查询开关配置列表
     * @param type 类型
     * @param name 参数名称
     * @param status 状态
     * @return
     */
    public PageInfo<SysConfigDto> listSysConfig(Integer type, String name, Integer status,int pageNum,int pageSize) {
        Map<String, Object> params = new HashMap();
        params.put("type", type);
        params.put("name", name);
        params.put("status", status);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        PageHelper.startPage(pageNum, pageSize);

        PageInfo<SysConfigDto> pageInfos = sysConfigMapper.listSysConfig(params).toPageInfo();
        return pageInfos;
    }

    /**
     * 根据id查询配置信息
     * @param id 主键id
     * @return
     */
    public SysConfigDto getSysConfig(Long id) {
        Map<String, Object> params = new HashMap();
        params.put("id", id);
        params.put("pageNum", 1);
        params.put("pageSize", 1);
        PageHelper.startPage(1, 1, false);
        PageInfo<SysConfigDto> pageInfos = sysConfigMapper.listSysConfig(params).toPageInfo();
        if(pageInfos.getSize() > 0) {
            return pageInfos.getList().get(0);
        } else {
            return new SysConfigDto();
        }
    }

    /**
     * 新增或修改 配置
     * @param dto
     */
    public void saveSysConfig(SysConfigDto dto) {
        if(dto.getId() == null) {
            //id为空，则是新增操作
            sysConfigMapper.insertSysConfig(dto);
        } else {
            //id不为空，则是修改操作
            sysConfigMapper.updateSysConfig(dto);
        }
        if(dto.getStatus() != null && dto.getCode() != null && dto.getValue() != null) {
            if(dto.getStatus() == 1) {
                //状态是启用状态的，添加redis缓存
                redisService.set(Constant.CONFIG_CODE_PRE + dto.getCode(), dto.getValue(), -1);
            } else {
                //状态是禁用状态的，删除redis缓存
                redisService.del(Constant.CONFIG_CODE_PRE + dto.getCode());
            }
        }
    }

    /**
     * 删除配置
     * @param code 配置编号
     */
    public void deleteSysConfig(String code) {
        sysConfigMapper.deleteSysConfig(code);
        redisService.del(Constant.CONFIG_CODE_PRE + code);
    }
}
