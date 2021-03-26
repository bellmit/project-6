package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视频mapper
 */
@DS("xy-db")
public interface FirstVideosMapper {

    /**
     * 检查视频是否上线
     * @param videoIds
     * @return 返回上线的视频ID
     */
    List<Integer> checkVideosIsOnline(@Param("videoIds") List<Integer> videoIds);
}
