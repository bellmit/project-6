package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.vo.HotListConfVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HotListMapper {
    /**
     *  根据热榜类型查询对应热榜数据
     *
     * @param type
     * @return
     */
    List<HotListVo> queryHotListByType(@Param("type") Integer type);

    /**
     * 获取榜单banner配置
     *
     * @return
     */
    HotListConfVo getHotListConf();
}
