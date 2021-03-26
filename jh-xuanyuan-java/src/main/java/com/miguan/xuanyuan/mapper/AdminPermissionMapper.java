package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.dto.PermissionListDto;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.vo.AdminPermissionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
public interface AdminPermissionMapper extends BaseMapper<AdminPermission> {
    AdminPermission getPermissionExcludeId(String actionCode, Long id);

    List<AdminPermissionVo> getPermissionListByParentId(Long parentId);

    Integer existChildren(Long parentId);

    Integer projectHasChildren(Long projectId);

    List<PermissionListDto> listPermissionByParentId(@Param("parentId") Integer parentId, @Param("projectId") Integer projectId);
}
