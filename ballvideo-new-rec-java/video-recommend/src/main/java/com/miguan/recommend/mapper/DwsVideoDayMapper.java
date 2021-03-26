package com.miguan.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DwsVideoDayMapper extends BaseMapper {

    /**
     * 查询指定日期，高于指定曝光量的视频
     *
     * @param dt         日期  yyyyMMDD
     * @param lowestShow 最低曝光量
     * @return
     */
    public List<Map<String, Object>> findTopVideoInOneDay(@Param("dt") Integer dt, @Param("lowestShow") Integer lowestShow);

    /**
     * 查询指定日期，高于指定曝光量的分类视频
     *
     * @param dt         日期  yyyyMMDD
     * @param lowestShow 最低曝光量
     * @param catId      分类ID
     * @return
     */
    public List<Map<String, Object>> findTopVideoWithCatId(@Param("dt") Integer dt, @Param("lowestShow") Integer lowestShow, @Param("catId") Integer catId);
}
