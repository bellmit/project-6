package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频类别表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface VideoLeaderboardMapper extends BaseMapper {

    @DS("report-db")
    public int insertBatch(@Param("dataList") List<Map<String, Object>> dataList);

    @DS("report-db")
    public int deleteData(Map<String, Object> param);

    @DS("report-db")
    public List<Map<String, Object>> findViewLeaderboard(Map<String, Object> param);

    @DS("report-db")
    public Map<String, Object> findViewNumberByVideoId(Map<String, Object> param);

    @DS("report-db")
    public List<Integer> findDistinctVideoidsFromOneDay(@Param("type") Integer type, @Param("date") String date);

}
