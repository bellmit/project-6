package com.miguan.ballvideo.service.impl;

import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.entity.CommentReplyRequest;
import com.miguan.ballvideo.entity.CommentReplyResponse;
import com.miguan.ballvideo.entity.VideosShare;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.mapper.CommentReplyMapper;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.mapper.SmallVideosMapper;
import com.miguan.ballvideo.repositories.VideosShareJpaRepository;
import com.miguan.ballvideo.service.VideoShareService;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.SmallVideosVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * 视频分享ServiceImpl
 *
 * @author xy.chen
 * @date 2019-09-09
 **/

@Slf4j
@Service("videoShareService")
public class VideoShareServiceImpl implements VideoShareService {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private SmallVideosMapper smallVideosMapper;

    @Resource
    private ClUserMapper clUserMapper;

    @Resource
    private CommentReplyMapper commentReplyMapper;

    @Resource
    private VideosShareJpaRepository videosShareJpaRepository;

    @Override
    public Map<String, Object> getShareVideos(String type, String videoId, String userId, String videoType, String catId) {
        Map<String, Object> result = new HashMap<>();
        //首页视频
        if ("10".equals(type)) {
            //视频详情信息
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("id", videoId);
            paraMap.put("state", "1");//状态 1开启 2关闭

            List<FirstVideos> videosVos = null;
            if (userId == null || "0".equals(userId)) {
                if(StringUtils.isEmpty(videoId) || "0".equals(videoId)){
                    log.error("searchDebug0429(getShareVideos):catId="+catId);
                    videosVos = null;
                }else{
                    videosVos = firstVideosMapper.findFirstVideosList(paraMap);
                }
            } else {
                paraMap.put("userId", userId);
                videosVos = firstVideosMapper.findFirstVideosListByUserId(paraMap);
            }

            if(CollectionUtils.isNotEmpty(videosVos)){
                //VideoUtils.cntFirstVideoLoveAndWatchNum(videosVos);
                FirstVideos firstVideosVo = videosVos.get(0);
                result.put("firstVideosVo", firstVideosVo);
            }else{
                result.put("firstVideosVo", null);
            }
            //评论信息
            CommentReplyRequest commentReply = new CommentReplyRequest();
            commentReply.setReplyType(1);
            commentReply.setVideoId(Long.valueOf(videoId));
            commentReply.setVideoType(Integer.valueOf(type));
            List<CommentReplyResponse> oneCommentReplyList = commentReplyMapper.findOneCommentReply(commentReply);
            CommentReplyResponse oneCommentReply = null;
            if (oneCommentReplyList != null && oneCommentReplyList.size() > 0) {
                oneCommentReply = oneCommentReplyList.get(0);
                int likeNum = 300 + new Random().nextInt(701);
                oneCommentReply.setLikeNum((long) likeNum);
            }
            result.put("commentReply", oneCommentReply);

            //视频列表信息(排除播放)
            Map<String, Object> params = new HashMap<>();
            if ("20".equals(videoType)) {
                params.put("catId", catId);
            }
            params.put("excludeId", videoId);//排除播放视频
            params.put("state", "1");//状态 1开启 2关闭
            params.put("videoType", "10");
            PageHelper.startPage(1, 10);

            List<FirstVideos> firstVideosList = null;
            if (userId == null || "0".equals(userId)) {
                firstVideosList = firstVideosMapper.findFirstVideosList(params);
            } else {
                params.put("userId", userId);
                firstVideosList = firstVideosMapper.findFirstVideosListByUserId(params);
            }
            /*if (CollectionUtils.isNotEmpty(firstVideosList)) {
                VideoUtils.cntFirstVideoLoveAndWatchNum(firstVideosList);
            }*/
            result.put("firstVideosList", firstVideosList);
        } else if ("20".equals(type)) {
            //小视频详情信息
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("id", videoId);
            paraMap.put("state", "1");//状态 1开启 2关闭

            List<SmallVideosVo> smallVideos = null;
            if (userId == null || "0".equals(userId)) {
                smallVideos = smallVideosMapper.findSmallVideosList(paraMap);
            } else {
                paraMap.put("userId", userId);
                smallVideos = smallVideosMapper.findSmallVideosListByUserId(paraMap);
            }

            SmallVideosVo smallVideo = new SmallVideosVo();
            if (CollectionUtils.isNotEmpty(smallVideos)) {
                VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideos);
                smallVideo = smallVideos.get(0);
            }
            //随机生成收藏数、点赞数、评论数
            int collectionCount = 300 + new Random().nextInt(201);//收藏数
            int commentCount = 500 + new Random().nextInt(501);//评论数
            int loveCount = 1000 + new Random().nextInt(4001);//点赞数
            smallVideo.setCollectionCount(String.valueOf(collectionCount));
            smallVideo.setCommentCount(String.valueOf(commentCount));
            smallVideo.setLoveCount(String.valueOf(loveCount));
            result.put("smallVideo", smallVideo);
        }
        //用户信息
        if("0".equals(userId) || StringUtils.isEmpty(userId)){
            result.put("clUserVo", null);
        }else{
            Map<String, Object> userParam = new HashMap<>();
            userParam.put("id", userId);
            List<ClUserVo> userVos = clUserMapper.findClUserList(userParam);
            if(CollectionUtils.isNotEmpty(userVos)){
                ClUserVo clUserVo = userVos.get(0);
                result.put("clUserVo", clUserVo);
            }else{
                result.put("clUserVo", null);
            }
        }
        return result;
    }

    @Transactional
    @Override
    public void shareVideosLog(Long videoId, Integer shareType) {
        VideosShare videosShare = videosShareJpaRepository.findByVideoIdAndShareType(videoId, shareType);
        if (videosShare == null) {
            videosShare = new VideosShare();
            videosShare.setVideoId(videoId);
            videosShare.setShareType(shareType);
            videosShare.setShareCount(1L);
            videosShare.setCreateDate(new Date());
            videosShareJpaRepository.save(videosShare);
        } else {
            videosShareJpaRepository.updateVideosShareCnt(videoId, shareType);
        }
    }

    @Override
    public int getShareVideosInfo(Long videoId, Integer shareType, Long number) {
        if (number == -1L) {
            return number.intValue();
        }
        VideosShare videosShare =  videosShareJpaRepository.findByVideoIdAndShareType(videoId, shareType);
        if (videosShare != null) {
            long shareCount = videosShare.getShareCount();
            number = Long.valueOf(number) - shareCount;
            if (number < 0L) {
                number = 0L;
            }
        }
        return number.intValue();
    }
}
