package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.Video;
import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.vo.ClCollectionVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 视频源列表Mapper
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

public interface VideoMapper {

    /**
     * 通过条件查询视频源列列表
     **/
    List<Video> findVideosList(Map<String, Object> params);

    /**
     * 修改视频收藏、分享数量
     **/
    int updateCount(Map<String, Object> params);

    /**
     * 设置收藏类型
     *
     * @param params
     * @return
     */
    int updateType(Map<String, Object> params);

    /**
     * 通过条件查询收藏信息列表
     **/
    List<Map<String, Object>> findCollectionList(Map<String, Object> params);

    /**
     * 新增收藏信息
     *
     * @param clCollectionVo
     * @return
     */
    int saveCollection(ClCollectionVo clCollectionVo);

    /**
     * 删除收藏信息
     *
     * @param params
     * @return
     */
    int delCollection(Map<String, Object> params);

    /**
     * 获得分享视频列表(分享视频，以及热门前8的视频)
     *
     * @param videoId 分享视频的id
     * @return
     */
    List<Map<String, Object>> gainShareVideos(String videoId);

    /**
     * 保存上传视频信息
     *
     * @param videosVo
     * @return
     */
    int saveUploadVideos(Video videosVo);

    /**
     * 批量删除上传视频信息
     *
     * @param uploadVideosIds
     * @return
     */
    int batchDelUploadVideos(@Param("uploadVideosIds") String[] uploadVideosIds);

    Video findOne(Map<String, Object> params);

    /**
     * 查询审核通过最新视频的创建时间
     * @return
     */
    @Select(value="select max(created_at) from videos where bsy_url != '' AND bsy_img_url != '' AND state = 1 AND recommend = 1 AND approval_state = 2")
    Date queryNewVideosTime();

    /**
     * 根据渠道查询推荐内容（热门V2.8.0）
     **/
    List<Video> findVideosListByChannel(Map<String, Object> params);


    /**
     * 根据渠道查询推荐内容（统计）
     **/
    Long countVideosListByChannelDoublePage(Map<String, Object> params);
    /**
     * 根据渠道查询推荐内容（热门V2.8.0）  双页查询
     **/
    List<Video> findVideosListByChannelDoublePage(Map<String, Object> params);

    /**
     * 根据 id 获得对象
     * @param id
     * @return
     */
    Video findVideoById(Long id);

    /**
     * 根据感兴趣标签查询200条视频ID
     **/
    @Cacheable(value = CacheConstant.INTEREST_VIDEO_ID_LIST, unless = "#result == null || #result.size()==0")
    List<Long> findVideosIdList(Map<String, Object> params);

    /**
     * 查询视频ID总数
     * @param params
     * @return
     **/
    @Cacheable(value = CacheConstant.VIDEO_ID_COUNT, unless = "#result == null")
    Integer findVideosIdCount(Map<String, Object> params);

    /**
     * 根据视频ID查询推荐内容
     **/
    List<Video> findVideosListById(Map<String, Object> params);
}