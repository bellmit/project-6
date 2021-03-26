package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.pojo.AdAdvertTestConfig;
import com.miguan.advert.domain.pojo.AdTestCodeRelation;
import com.miguan.advert.domain.vo.result.AdvertConfigAndCodeVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MigratingDataMapper {

    /**
     * 查询所有的广告配置List
     * @return
     */
    @Select("SELECT * FROM ad_advert_config")
    List<AdvertConfigAndCodeVo> getAllAdAdvertConfigList();

    /**
     * 查询所有的广告配置代码位List
     * @return
     */
    @Select("SELECT * FROM ad_advert_config_code")
    List<AdvertConfigAndCodeVo> getAllAdAdvertConfigCodeList();

    /**
     * 获取流量广告表的数据条数（拼装主键ID用）
     * @return
     */
    @Select("SELECT COUNT(*) FROM ad_advert_flow_config")
    Integer getAdAdvertFlowConfigCount ();

    /**
     * 获取实验广告表的数据条数（拼装主键ID用）
     * @return
     */
    @Select("SELECT COUNT(*) FROM ad_advert_test_config")
    Integer getAdAdvertTestConfigCount ();

    /**
     * 批量插入流量广告表数据
     * @param adAdvertFlowConfigList
     * @return
     */
    Integer insertBatchAdAdvertFlowConfig(@Param("adAdvertFlowConfigList")List<AdAdvertFlowConfig> adAdvertFlowConfigList);

    /**
     * 批量插入实验广告表数据
     * @param adAdvertTestConfigList
     * @return
     */
    Integer insertBatchAdAdvertTestConfig(@Param("adAdvertTestConfigList")List<AdAdvertTestConfig> adAdvertTestConfigList);

    /**
     * 批量插入实验广告与代码位关系表数据
     * @param adTestCodeRelationList
     * @return
     */
    Integer insertBatchAdTestCodeRelation(@Param("adTestCodeRelationList")List<AdTestCodeRelation> adTestCodeRelationList);

    /**
     * 删除流量分组
     * @return
     */
    @Delete("delete from ad_advert_flow_config")
    int deleteFlow();

    /**
     * 删除实验分组
     * @return
     */
    @Delete("delete from ad_advert_test_config")
    int deleteTest();

    /**
     * 删除实验代码位关联表信息
     * @return
     */
    @Delete("delete from ad_test_code_relation")
    int deleteConfig();


    /**
     * 查询流量分组,有实验的信息
     * @return
     */
    @Select("select * from ad_advert_flow_config where ab_flow_id is not null")
    List<AdAdvertFlowConfig> findFlowConfigWithAb();


    /**
     * 查询流量分组,仅有AB默认分组的流量信息
     * @return
     */
    @Select("select * from ad_advert_flow_config config group by position_id,test_state having  position_id not in  ( " +
            " select position_id from ad_advert_flow_config c where test_state = 0 group by position_id " +
            " ) ")
    List<AdAdvertFlowConfig> searchOnlyAb();

    /**
     * 查询流量分组,仅有AB默认分组的流量信息
     * @return
     */
    @Select("select MIN(id) from ad_advert_flow_config config group by position_id,test_state having  position_id not in  ( " +
            " select position_id from ad_advert_flow_config c where test_state = 0 group by position_id " +
            " ) ")
    List<Integer> searchWithoutDefault();

    /**
     * 删除实验代码位关联表信息
     * @return
     * @param abIds
     */
    @Delete({
        "<script>",
            " delete from ad_test_code_relation where config_id in (select ac.id from ad_advert_test_config ac ",
                    " inner join ad_advert_flow_config af on ac.flow_id = af.id where (af.test_state = 1 and af.id not in ",
                "<foreach collection='abIds' item='abId' open='(' separator=',' close=')'>",
                    "#{abId}",
                "</foreach>",
                " or ac.type = 2 )  ",
                "<if test='positionId != null'>",
                " AND (af.position_id = #{positionId}) ",
                "</if> ) ",
        "</script>"
    })
//    @Delete("delete from ad_test_code_relation where config_id in (select ac.id from ad_advert_test_config ac inner join ad_advert_flow_config af on ac.flow_id = af.id where af.test_state = 1)")
    int deleteAbConfig(@Param("abIds") List<Integer> abIds,@Param("positionId") Integer positionId);


    /**
     * 删除实验代码位关联表信息
     * @return
     */
    @Delete("delete from ad_test_code_relation where config_id in (select ac.id from ad_advert_test_config ac inner join ad_advert_flow_config af on ac.flow_id = af.id where af.test_state = 1)")
    int deleteAbConfigEpt();

    /**
     * 删除实验分组
     * @return
     * @param abIds
     */
    @Delete({
            "<script>",
            " delete from ad_advert_test_config where flow_id in (select id from ad_advert_flow_config where test_state = 1 and id not in ",
                "<foreach collection='abIds' item='abId' open='(' separator=',' close=')'>",
                    "#{abId}",
                "</foreach>",
                "<if test='positionId != null'>",
                " AND position_id = #{positionId} ",
                "</if>",
            "  ) " ,
                    "<if test='positionId == null'>",
                    " or type = 2  ",
                    "</if>",
                    " ",
            "</script>"
    })
//    @Delete("delete from ad_advert_test_config where flow_id in (select id from ad_advert_flow_config where test_state = 1 ) ")
    int deleteAbTest(@Param("abIds") List<Integer> abIds,@Param("positionId") Integer positionId);

    @Delete("delete from ad_advert_test_config where flow_id in (select id from ad_advert_flow_config where test_state = 1 ) ")
    int deleteAbTestEpt();
    /**
     * 删除流量分组
     * @return
     * @param abIds
     */
    @Delete({
            "<script>",
            " delete from ad_advert_flow_config where test_state = 1 and id not in ",
                "<foreach collection='abIds' item='abId' open='(' separator=',' close=')'>",
                    "#{abId}",
                "</foreach>",
                "<if test='positionId != null'>",
                " AND position_id = #{positionId} ",
                "</if>",
            " ",
            "</script>"
    })
    //@Delete("delete from ad_advert_flow_config where test_state = 1")
    int deleteAbFlow(@Param("abIds") List<Integer> abIds,@Param("positionId") Integer positionId);

    /**
     * 删除流量分组
     * @return
     */
    @Delete("delete from ad_advert_flow_config where test_state = 1")
    int deleteAbFlowEpt();

    @Update({
            "<script>",
            " update ad_advert_flow_config set name='默认分组',ab_flow_id = null,test_state = 0 where test_state = 1 ",
            "<if test='positionId != null'>",
            " AND position_id = #{positionId} ",
            "</if>",
            " ",
            "</script>"
    })
//    @Update("update ad_advert_flow_config set name='默认分组',ab_flow_id = null,test_state = 0 where test_state = 1 ")
    void updateAbFlowInfo(@Param("positionId") Integer positionId);

    @Update({
            "<script>",
            " update ad_advert_test_config set ab_test_id = null where ab_test_id is not null ",
            "<if test='positionId != null'>",
            "  and flow_id in (SELECT id from ad_advert_flow_config t where  position_id = #{positionId} ) ",
            "</if>",
            " ",
            "</script>"
    })
//    @Update("update ad_advert_test_config set ab_test_id = null where ab_test_id is not null ")
    void updateAbTestInfo(@Param("positionId") Integer positionId);


    @Insert({ " insert into ad_advert_flow_config (id,name,position_id,ab_flow_id,type,test_state,created_at,updated_at,state) " +
            " select id,name,position_id,ab_flow_id,type,test_state,created_at,updated_at,state from ad_advert_flow_config_copy " })
    void copyFlow();

    @Insert({ " insert into ad_advert_test_config (id,flow_id,ab_test_id,computer,type,created_at,updated_at,state) " +
            "select id,flow_id,ab_test_id,computer,type,created_at,updated_at,state from ad_advert_test_config_copy " })
    void copyTest();

    @Insert({ " insert into ad_test_code_relation (id,config_id,code_id,number,matching,order_num,state,created_at,updated_at) " +
            " select id,config_id,code_id,number,matching,order_num,state,created_at,updated_at from ad_test_code_relation_copy " })
    void copyConfig();
}
