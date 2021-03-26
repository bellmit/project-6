package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.pojo.AdAdvertCode;
import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.pojo.AdAdvertTestConfig;
import com.miguan.advert.domain.pojo.AdTestCodeRelation;
import com.miguan.advert.domain.vo.request.AdAdvertFlowConfigVo;
import com.miguan.advert.domain.vo.request.AdAdvertTestConfigVo;
import com.miguan.advert.domain.vo.result.FlowCountVo;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

public interface FlowTestMapper {

    /**
     * 查询通过对应的id
     * @param id
     * @return
     */
    @Select("select * from ad_advert_flow_config where id = #{id}")
    AdAdvertFlowConfigVo findAdFlowConfigByIdVo(@Param("id") String id);


    /**
     * 查询通过对应的id
     * @param id
     * @return
     */
    @Select("select * from ad_advert_flow_config where id = #{id}")
    AdAdvertFlowConfig findAdFlowConfigById(@Param("id") String id);

    /**
     * 查询通过对应的id
     * @param id
     * @return
     */
    @Select("select * from ad_advert_test_config where id = #{id}")
    AdAdvertTestConfigVo findAdTestConfigById(@Param("id") String id);

    /**
     * 查询通过对应的id
     * @param abTestId
     * @return
     */
    @Select("select * from ad_advert_test_config where ab_test_id = #{abTestId}")
    List<AdAdvertTestConfig> findAdTestConfigByAbTestId(@Param("abTestId") String abTestId);

    /**
     * 查询通过对应的id
     * @param configId
     * @return
     */
    @Select("select * from ad_test_code_relation where config_id = #{configId}")
    List<AdTestCodeRelation> findAdTestCodeRelationByConfigId(@Param("configId") String configId);

    /**
     * 根据广告位所有的exp_code
     * @param positionId
     * @return
     */
    List<String> findAllCodeByPositionId(@Param("positionId") Integer positionId);


    /**
     * 查询代码位
     * @param dataMap
     * @return
     */
    @Select({
            "<script>",
            "select id,put_in from ad_advert_code where id in",
            "<foreach collection='dataMap.entrySet()' item='value' index='key' open='(' separator=',' close=')'>",
                "#{key}",
            "</foreach>",
            "</script>"
    })
    List<Map<String,Object>> findAdCodeByIds(@Param("dataMap") Map<String,String> dataMap);

    /**
     * 修改流量分组
     * @param config
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "insert into ad_advert_flow_config(name, position_id,ab_flow_id,type,test_state,created_at,state,exp_code,pub_time,status) " +
            "values (#{name}, #{position_id}, #{ab_flow_id}, #{type},#{test_state}, NOW(),#{state},#{exp_code},#{pub_time},#{status})" })
    int insertFlowGroup(AdAdvertFlowConfig config);


    /**
     * 修改流量分组
     * @param config
     * @return
     */
    @Update("update ad_advert_flow_config set name = #{name}, ab_flow_id = #{ab_flow_id}, updated_at = NOW() where id = #{id}")
    int updateFlowGroup(AdAdvertFlowConfig config);

    /**
     * 修改流量分组的状态
     * @param ab_flow_id
     * @return
     */
    @Update("update ad_advert_flow_config set status = #{status} where ab_flow_id = #{ab_flow_id}")
    int updateStatus(Integer status , String ab_flow_id);

    /**
     * 修改流量分组的状态
     * @param id
     * @return
     */
    @Update("update ad_advert_flow_config set state = #{state},status = #{status} where id = #{id}")
    int updateStatusById(Integer state ,Integer status , String id);

    /**
     * 修改流量测试分组的状态
     * @param id
     * @return
     */
    @Update("update ad_advert_test_config set state = #{state},updated_at = NOW() where id = #{id}")
    int updateTestStatusById(Integer state , String id);

    /**
     * 修改流量关联分组的状态
     * @param configId
     * @return
     */
    @Update("update ad_test_code_relation set state = #{state},updated_at = NOW() where config_id = #{configId}")
    int updateRelationStatusById(Integer state , String configId);


    /**
     * 修改流量分组
     *
     * @param pubTime 更新时间
     * @param flowId  分组id
     * @param status
     * @return
     */
    @Update("update ad_advert_flow_config set name = #{name},pub_time = #{pubTime},status = #{status}, updated_at = NOW() where id = #{flowId}")
    int updateInfo(String name, String pubTime, String flowId, Integer status);

    /**
     * 修改流量分组
     *
     * @return
     */
    @Update("update ad_advert_flow_config set name = #{name}, updated_at = NOW() where id = #{flowId}")
    int updateName(String name, String flowId, Integer status);


    /**
     * 新增实验分组
     * @param config
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "insert into ad_advert_test_config(flow_id, ab_test_id,computer,type,created_at,state) values " +
            "(#{flow_id}, #{ab_test_id}, #{computer}, #{type}, NOW(),#{state})" })
    int insertTestGroup(AdAdvertTestConfig config);

    /**
     * 新增实验分组和代码位关联
     * @param rela
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({ "insert into ad_test_code_relation(config_id, code_id, number, matching, order_num, created_at,state) values " +
            "(#{config_id}, #{code_id}, #{number}, #{matching}, #{order_num}, NOW(), #{state})" })
    int insertRelaGroup(AdTestCodeRelation rela);

    /**
     * 删除流量分组
     * @param id
     * @return
     */
    @Delete("delete from ad_advert_flow_config  where id = #{id}")
    int deleteFlowById(@Param("id") String id);

    /**
     * 删除实验分组
     * @param id
     * @return
     */
    @Delete("delete from ad_advert_test_config  where id = #{id}")
    int deleteTestById(@Param("id") String id);

    /**
     * 删除实验代码位关联表信息
     * @param configId
     * @return
     */
    @Delete("delete from ad_test_code_relation  where config_id = #{configId}")
    int deleteConfigByConfId(@Param("configId") String configId);


    /**
     * 修改流量分组测试组状态
     * @param flowId
     * @param testState
     * @return
     */
    @Update("update ad_advert_flow_config set test_state = #{testState}, updated_at = NOW() where id = #{flowId}")
    int updateFlowTestState(@Param("flowId") String flowId, @Param("testState") String testState);

    /**
     * 修改实验分组策略
     * @param computer
     * @param testId
     * @return
     */
    @Update("update ad_advert_test_config set computer = #{computer}, updated_at = NOW() where id = #{testId}")
    int updateTestComputer(@Param("computer") String computer, @Param("testId") String testId);

    /**
     * 修改实验分组类型：分组类型：0:默认分组, 1：对照组, 2:测试组
     * @param type
     * @param testId
     * @return
     */
    @Update("update ad_advert_test_config set type = #{type}, updated_at = NOW() where ab_test_id = #{testId}")
    int updateTestType(@Param("type") String type, @Param("testId") String testId);

    /**
     * 查询流量分组配置表
     * @return
     */
    List<AdAdvertFlowConfig> getAdvFlowConfLst(@Param("positionId") String positionId,
                                               @Param("type") String type, @Param("flowId") String flowId, @Param("abFlowId") String abFlowId);

    /**
     * 批量查询流量分组配置表
     * @return
     */
    List<FlowCountVo> getAllAdvFlowConfLst(@Param("positionIds") String positionIds);

    /**
     * 查询代码位列表
     * @param positionId
     * @return
     */
    List<AdAdvertCode> getAdvCodeInfoVoLst(String positionId);
    /**
     * 获取实验分组
     * @param flowId
     * @param type
     * @return
     */
    List<AdAdvertTestConfig> getAdvTestConfLst(String flowId, String type);


    /**
     * 获取实验组配置策略信息
     * @param configId
     * @return
     */
    List<AdTestCodeRelation> getTestCodeRelaLst(String configId,Integer computer);



    /**
     * 获取根据广告位置ID获取app_package
     * @param posId
     * @return
     */
    @Select("select app_package from ad_advert_position where id = #{posId} ")
    List<String> findAppPackageByPosId(@Param("posId") String posId);



    @Select({
            "<script>",
                "select distinct code_id from ad_test_code_relation where state = 1 and code_id in",
                "<foreach collection='codeIds' item='codeId' open='(' separator=',' close=')'>",
                    "#{codeId}",
                "</foreach>",
            "</script>"
    })
    List<Map> findTestCodeRelaByCodeId(@Param("codeIds") List<String> codeIds);

    @Update({
            "<script>",
                "<foreach collection='dataMap.entrySet()' item='value' index='key' separator=';' >",
                    "update ad_advert_code set put_in = #{value}, updated_at = NOW() where id = #{key}",
                "</foreach>",
            "</script>"
            })
    int updateCodePutInByDataMap(@Param("dataMap") Map<String,String> dataMap);

    List<AdTestCodeRelation> queryDefalutRelation(Integer positionId);

    AdAdvertTestConfig getDefaultAdvTestConf(Integer positionId);

    /**
     * 修改实验分组策略
     * @param adTestConfigList
     * @return
     */
    @Update({
            "<script>",
            "<foreach collection='adTestConfigList' item='adTestConfig' index='key' separator=';' >",
                    "update ad_advert_test_config set ab_first_load_position = #{adTestConfig.abFirstLoadPosition},ab_second_load_position = #{adTestConfig.abSecondLoadPosition}, " ,
                    " ab_max_show_num = #{adTestConfig.abMaxShowNum} ,ab_custom_rule1 = #{adTestConfig.abCustomRule1},ab_custom_rule2 = #{adTestConfig.abCustomRule2}, " ,
                    " ab_custom_rule3 = #{adTestConfig.abCustomRule3} , ladder_delay_millis = #{adTestConfig.ladderDelayMillis} , ",
                    " common_delay_millis = #{adTestConfig.commonDelayMillis} , updated_at = NOW() where id = #{adTestConfig.id}",
            "</foreach>",
            "</script>"
    })
    int updateBatchTestInfo(@Param("adTestConfigList") List<AdAdvertTestConfigVo> adTestConfigList);

    @Update({
            "<script>",
            "<foreach collection='dataMap.entrySet()' item='value' index='key' separator=';' >",
            "update ad_advert_flow_config set open_status = #{value}, updated_at = NOW() where id = #{key}",
            "</foreach>",
            "</script>"
    })
    int updateBatchOpenStatusById(@Param("dataMap") Map<String,Integer> dataMap);
}
