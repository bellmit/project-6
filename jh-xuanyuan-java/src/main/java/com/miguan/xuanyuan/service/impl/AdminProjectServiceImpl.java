package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.entity.AdminProject;
import com.miguan.xuanyuan.mapper.AdminPermissionMapper;
import com.miguan.xuanyuan.mapper.AdminProjectMapper;
import com.miguan.xuanyuan.service.AdminProjectService;
import com.miguan.xuanyuan.vo.AdminProjectVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 项目表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Service
public class AdminProjectServiceImpl extends ServiceImpl<AdminProjectMapper, AdminProject> implements AdminProjectService {

    @Resource
    private AdminProjectMapper mapper;

    public List<AdminProjectVo> getProjectList() {
        return mapper.getProjectList();
    }
}
