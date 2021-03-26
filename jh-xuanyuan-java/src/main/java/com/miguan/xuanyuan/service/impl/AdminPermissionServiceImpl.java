package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.dto.PermissionListDto;
import com.miguan.xuanyuan.entity.AdminPermission;
import com.miguan.xuanyuan.mapper.AdminPermissionMapper;
import com.miguan.xuanyuan.mapper.AdminProjectMapper;
import com.miguan.xuanyuan.mapper.IdentityMapper;
import com.miguan.xuanyuan.service.AdminPermissionService;
import com.miguan.xuanyuan.service.AdminProjectService;
import com.miguan.xuanyuan.vo.AdminPermissionVo;
import com.miguan.xuanyuan.vo.AdminProjectVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Service
public class AdminPermissionServiceImpl extends ServiceImpl<AdminPermissionMapper, AdminPermission> implements AdminPermissionService {

    @Resource
    private AdminPermissionMapper mapper;

    @Resource
    private AdminProjectMapper adminProjectMapper;

    public void saveData(AdminPermission adminPermission) {
        if (adminPermission.getId() != null) {
            mapper.updateById(adminPermission);
        } else {
            mapper.insert(adminPermission);
        }
    }

    /**
     * 根据操作标识获取数据
     *
     * @param actionCode
     * @param id
     * @return
     */
    public AdminPermission getPermissionExcludeId(String actionCode, Long id) {
        return mapper.getPermissionExcludeId(actionCode, id);
    }

    public List<AdminPermissionVo> getPermissionListByParentId(Long parentId) {
        return mapper.getPermissionListByParentId(parentId);
    }

    /**
     * 是否存在孩子
     *
     * @param parentId
     * @return
     */
    public Integer existChildren(Long parentId) {
        return mapper.existChildren(parentId);
    }

    public Integer projectHasChildren(Long projectId) {
        return mapper.projectHasChildren(projectId);
    }

    public List<PermissionListDto> listTreePermission() {
        List<PermissionListDto> list = adminProjectMapper.listProject();  //获取项目列表
        for(PermissionListDto dto : list) {
            listChildren(dto);
        }
        return list;
    }

    private void listChildren(PermissionListDto parentPermission) {
        Integer parentId = parentPermission.getId();
        Integer projectId = parentPermission.getProjectId();
        parentId = (parentId == null || parentId < 0 ? 0 : parentPermission.getId());
        List<PermissionListDto> childrendList = mapper.listPermissionByParentId(parentId, projectId);
        parentPermission.setChildrenList(childrendList);  //设置子菜单列表
        //递归查询子菜单列表
        if(childrendList != null && !childrendList.isEmpty()) {
            for(PermissionListDto dto : childrendList) {
                this.listChildren(dto);
            }
        }
    }

}
