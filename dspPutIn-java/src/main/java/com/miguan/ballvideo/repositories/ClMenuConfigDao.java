package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.ClMenuConfig;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @Description: 菜单栏配置表JPA
* @Param:
* @return:
* @Author: hyl
* @Date:
*/
public interface ClMenuConfigDao extends JpaRepository<ClMenuConfig, Long> {
    /**
     *
     * 通过条件查询菜单栏配置列表
     * state    状态：0启用  1禁用
     **/
    @Query(value = "select * from cl_menu_config where app_package = ?1 and state = '0' and hide_channel not like CONCAT('%',?2,'%') order by sort asc",nativeQuery = true)
    List<ClMenuConfig> findClMenuConfigList(String appPackage,String channelId);


    /**
     *
     * 通过条件查询菜单栏配置列表
     * state    状态：0启用  1禁用
     **/
    @Cacheable(value = CacheConstant.FIND_CLMENUCONFIGLISTBYAPPPACKAGE, unless = "#result == null || #result.size()==0")
    @Query(value = "select * from cl_menu_config where app_package = ?1 and state = '0' and hide_channel not like CONCAT('%',?2,'%') and " +
            " concat(lpad(SUBSTRING_INDEX(?3,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(?3,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(?3,'.',-1), 3, '0')) + 0 "+
            " BETWEEN " +
            " concat(lpad(SUBSTRING_INDEX(start_version,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(start_version,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(start_version,'.',-1), 3, '0')) + 0 "+
            " and " +
            " concat(lpad(SUBSTRING_INDEX(end_version,'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(end_version,'.',-2),'.',1), 3, '0')," +
            " lpad(SUBSTRING_INDEX(end_version,'.',-1), 3, '0')) + 0 "+
            " order by sort asc ",
            nativeQuery = true)
    List<ClMenuConfig> findClMenuConfigListByAppPackage(String appPackage,String channelId,String AppVersion);
}
