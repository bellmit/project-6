package com.miguan.bigdata.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.bigdata.entity.NewUserSelection;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统参数Dao
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
public interface NewUserSelectionMapper extends BaseMapper {

    /**
     * 查询所有系统配置
     *
     * @return
     */
    @DS("xy-db")
    public List<NewUserSelection> findAll();

    @DS("ck-dw")
    public Map<String, Object> findCountInfoByVideoId(@Param("dt") Integer dt, @Param("videoId") Integer videoId);

    @DS("xy-db")
    public int updateCountInfo(NewUserSelection selection);

}
