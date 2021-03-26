package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.entity.AdminProject;
import com.miguan.xuanyuan.vo.AdminProjectVo;

import java.util.List;

/**
 * <p>
 * 项目表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
public interface AdminProjectService extends IService<AdminProject> {

    List<AdminProjectVo> getProjectList();
}
