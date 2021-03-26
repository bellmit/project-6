package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.xuanyuan.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 第三方广告数据mapper
 */
public interface ThirdPlatApiMapper {

    /**
     * 查询出要同步的媒体账号对应的第三方平台
     * @return
     */
    List<ThirdPlatVo> findPlatList();

    /**
     * 批量插入third_plat_data记录
     * @param list
     */
    void batchInsertPlatData(@Param("list") List<ThirdPlatDataVo> list);

    /**
     * 查询出在旧配置平台配置广告的应用包名
     * @return
     */
    @DS("adv")
    List<String> findOldAdvConfigPackage();

    /**
     * 查询出在旧配置平台配置广告的app信息
     * @param packageNameList
     * @return
     */
    List<Map<String, Object>> findOldAdvConfigApp(@Param("list") List<String> packageNameList);

    /**
     * 保存在轩辕配置的应用在第三方的广告数据到third_plat_data_total表
     * @param params
     */
    void saveXyThirdPlatDataTotal(Map<String, Object> params);

    /**
     * 查询出旧广告配置平台的代码位对应的广告位名称
     * @return
     */
    List<ThirdAdPostionVo> findOldAdIdAndTotalName(@Param("date") String date);

    /**
     * 查询出旧配置平台的third_plat_data_total数据
     * @param params
     * @return
     */
    List<ThirdPlatDataTotalVo> findXyThirdPlatDataTotal(Map<String, Object> params);

    /**
     * 保存旧广告配置平台的third_plat_data_total数据
     * @param list
     */
    void saveOldThirdPlatDataTotal(@Param("list") List<ThirdPlatDataTotalVo> list);

    /**
     * 根据日期查询出旧配置平台的广告配置
     * @param date
     * @return
     */
    @DS("adv")
    List<AdConfigLogVo> findOldAdvConfigList(@Param("date") String date);

    /**
     * 保存广告配置log信息
     * @param list
     */
    void saveAdConfigLog(@Param("list") List<AdConfigLogVo> list);

    /**
     * 保存轩辕广告配置log信息
     * @param date
     */
    void saveXyAdConfigLog(@Param("date") String date);

    Integer countDataTotal(Map<String, Object> params);

    @DS("data-server")
    Integer countGetReady(Map<String, Object> params);

    @DS("data-server")
    void insertGetReady(Map<String, Object> params);
}
