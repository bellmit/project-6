package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.dto.YesterDayUserKeepDto;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.vo.UserKeepVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户留存数据表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface RpUserKeepMapper extends BaseMapper<RpUserKeep> {

    List<UserKeepVo> getData(Map<String, Object> params);

    List<RpUserKeep> getTableData(Map<String, Object> params);

    int deleteBySd(@Param("sd") Integer sd, @Param("appType") Integer appType);

    int insertBatch(@Param("dataList") List<RpUserKeep> dataList);

    RpUserKeep findByDto(Map<String, Object> yesterDayUserKeepDto);

    int updateUserAndKeep1ById(@Param("id") Long id, @Param("user") Integer user, @Param("keep1") Integer keep1);

    int insertLdBatch(@Param("dataList") List<RpUserKeep> dataList);

    List<UserKeepVo> getNewUserKeep(Map<String, Object> params);
}
