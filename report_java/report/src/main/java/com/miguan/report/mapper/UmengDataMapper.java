package com.miguan.report.mapper;

import com.miguan.report.vo.UmengChannelVo;
import com.umeng.uapp.param.UmengUappChannelInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface UmengDataMapper {

    void batchInsertChannelData(@Param("channels") List<UmengChannelVo> channels);

    void deleteChannelData(@Param("date") String date, @Param("packageName") String packageName);
}
