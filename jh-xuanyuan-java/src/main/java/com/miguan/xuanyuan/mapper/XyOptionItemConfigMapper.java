package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 选项配置
 * @Date 2021/1/21
 **/
public interface XyOptionItemConfigMapper extends BaseMapper<XyOptionItemConfig> {
    int saveBatch(List<XyOptionItemConfig> itemList);
    int deleteBatch(@Param("configCode") String configCode,@Param("idList")  List<Long> idList);
    List<XyOptionItemConfig> findBatch(@Param("configCode") String configCode,@Param("idList")  List<Long> idList);
    List<XyOptionItemConfig> findByConfigCode(String configCode);
    int judgeExistItem(@Param("configCode") String configCode,@Param("itemKey") String itemKey);
    XyOptionItemConfig findById(Long id);
    List<Long> findDeleteIds(@Param("configCode") String configCode,@Param("idList")  List<Long> ids);
    XyOptionItemConfig findByCodeAndKey(@Param("configCode") String configCode, @Param("itemKey") String itemKey);
    List<XyOptionItemConfig> findOperationPlatType(@Param("configCodes") List<String> allConfigCode,@Param("configCode") String configCode);
}
