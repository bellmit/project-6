package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.LdInterestTag;
import com.miguan.laidian.entity.LdUserTagRelation;
import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.vo.LdInterestTagVo;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 11:11
 * @Description:
 */
public interface LdInterestTagMapper extends Mapper{

    /**
     * 获取全部标签
     * @return
     */
    @Select("select * from ld_interest_tag where is_delete = 0 order by sort desc")
    List<LdInterestTag> getAllTags();

    /**
     * 获取用户-标签关系
     * @return
     */
    @Select("select * from ld_user_tag_relation where user_id = #{userId}")
    List<LdUserTagRelation> getUserTagRelation(@Param("userId") String userId);

    /**
     * 获取用户标签
     * @param userId
     * @return
     */
    @Select("select a.* from ld_interest_tag a inner join ld_user_tag_relation b on a.id = b.tag_id where b.user_id = #{userId} and a.is_delete = 0")
    List<LdInterestTag> getUserTags(@Param("userId") String userId);

    /**
     * 新增用户标签
     * @param userId
     * @param tagId
     * @return
     */
    @Insert({"insert into ld_user_tag_relation (`user_id`, `tag_id`, `device_id`) values(#{userId}, #{tagId}, #{deviceId})"})
    Integer insertUserTag(@Param("userId") String userId, @Param("tagId") Integer tagId, @Param("deviceId")String deviceId);

    /**
     * 删除用标签
     * @param userId
     * @return
     */
    @Delete("delete from ld_user_tag_relation where `user_id` = #{userId}")
    Integer deleteUserTag(@Param("userId") String userId);

    /**
     * 删除标签，根基device_id
     * @param deviceId
     * @return
     */
    @Delete("delete from ld_user_tag_relation where `device_id` = #{deviceId}")
    Integer deleteDeviceTag(@Param("deviceId") String deviceId);

    /**
     * 获取用户标签
     * @param params
     * @return
     */
    @Cacheable(value = CacheConstant.USER_INTEREST_TAG, unless = "#result == null || #result.size()==0")
    List<LdInterestTagVo> getUserTagList(Map<String, Object> params);
}
