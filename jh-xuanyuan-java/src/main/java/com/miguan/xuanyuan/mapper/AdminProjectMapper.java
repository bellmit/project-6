package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.dto.PermissionListDto;
import com.miguan.xuanyuan.entity.AdminProject;
import com.miguan.xuanyuan.vo.AdminProjectVo;

import java.util.List;

/**
 * <p>
 * 项目表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
public interface AdminProjectMapper extends BaseMapper<AdminProject> {

    List<AdminProjectVo> getProjectList();

    List<PermissionListDto> listProject();
}
