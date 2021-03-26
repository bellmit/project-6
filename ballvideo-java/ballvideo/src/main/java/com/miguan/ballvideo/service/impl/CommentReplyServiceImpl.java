package com.miguan.ballvideo.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.*;
import com.miguan.ballvideo.mapper.*;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.repositories.CommentReplyDao;
import com.miguan.ballvideo.repositories.CommentTemplateDao;
import com.miguan.ballvideo.service.ClUserService;
import com.miguan.ballvideo.service.CommentReplyService;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.CommentReplyCountVo;
import com.miguan.ballvideo.vo.CommentReplyRequestVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.ballvideo.common.constants.CommentReplyContant.*;

/**
 * 评论信息回复ServiceImpl
 * @author HYL
 * @date 2019-08-09
 **/
@Slf4j
@Service
public class CommentReplyServiceImpl implements CommentReplyService {


    @Autowired
    CommentReplyDao commentReplyDao;

    @Resource
    CommentReplyMapper commentReplyMapper;

    @Resource
    FirstVideosMapper firstVideosMapper;

    @Resource
    SmallVideosMapper smallVideosMapper;

    @Resource
    ClUserCommentMapper cUserCommentMapper;

    @Resource
    ClUserOpinionMapper clUserOpinionMapper;

    @Resource
    private CommentTemplateDao commentTemplateDao;

    @Resource
    private ClUserService clUserService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ClUserMapper clUserMapper;

    @Resource
    private DynamicQuery dynamicQuery;
    //用户模板id：1-20
    private int minNum = 1;
    private int maxNum = 21;

    public final static String FIRST_COMMENT_REPLY = "FirstCommentReply_";

    @Override
    @Transactional
    public Page<CommentReplyResponse> findAllCommentReply(CommentReplyRequestVo comcmentReplyRequestVo) {
        // type类型为1的时候，根据视频id查询一级评论，2的时候查询其他人评论当前用户的评论，3的时候查一级评论下的所有二级评论
        List<ClUserComment> giveUpComments;
        List<CommentReplyResponse> commentReplyResponse = null;
        String userId = comcmentReplyRequestVo.getUserId();
        int type = comcmentReplyRequestVo.getType();
        int currentPage = comcmentReplyRequestVo.getCurrentPage();
        int pageSize = comcmentReplyRequestVo.getPageSize();
        CommentReplyRequest commentReply = new CommentReplyRequest();
        BeanUtils.copyProperties(comcmentReplyRequestVo,commentReply);
        //  如果用户id为空时，查看评论数据，不展示是否点赞过的评论
        if(!("0").equals(userId)){
            giveUpComments = cUserCommentMapper.findGiveUpComments(userId);
            // type类型为1的时候，根据视频id查询一级评论
            if(REQUEST_TYPE_ONE==type){
                commentReply.setReplyType(FIRST_LEVEL_COMMENT);
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findAllCommentReply(commentReply);
                return (Page<CommentReplyResponse>) this.addGiveType(commentReplyResponse,giveUpComments);
                //2的时候查询其他人评论当前用户的评论
            }else if(REQUEST_TYPE_TWO==type){
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findMessageCenter(commentReply);
                commentReplyMapper.updateCommentReplyByPComment(commentReply.getToFromUid());
                return (Page<CommentReplyResponse>) commentReplyResponse;
                //3的时候查一级评论下的所有二级评论
            }else if(REQUEST_TYPE_THREE==type){
                commentReply.setReplyType(TWO_LEVEL_COMMENT);
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findAllCommentReplyTow(commentReply);
                return (Page<CommentReplyResponse>) this.addGiveType(commentReplyResponse,giveUpComments);
            }
            return (Page<CommentReplyResponse>) commentReplyResponse;
        }else {
            // type类型为1的时候，根据视频id查询一级评论
            if(REQUEST_TYPE_ONE==type){
                commentReply.setReplyType(FIRST_LEVEL_COMMENT);
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findAllCommentReply(commentReply);
                return (Page<CommentReplyResponse>) commentReplyResponse;
                //2的时候查询其他人评论当前用户的评论
            }else if(REQUEST_TYPE_TWO==type){
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findMessageCenter(commentReply);
                int i = commentReplyMapper.updateCommentReplyByPComment(commentReply.getToFromUid());
                return (Page<CommentReplyResponse>) commentReplyResponse;
                //3的时候查一级评论下的所有二级评论
            }else if(REQUEST_TYPE_THREE==type){
                commentReply.setReplyType(TWO_LEVEL_COMMENT);
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findAllCommentReplyTow(commentReply);
                return (Page<CommentReplyResponse>) commentReplyResponse;
            }
        }
        return (Page<CommentReplyResponse>) commentReplyResponse;
    }

    public List<CommentReplyResponse> addGiveType(List<CommentReplyResponse> commentReplyResponse, List<ClUserComment> giveUpComments){
        if(giveUpComments == null || giveUpComments.size()==0){
            commentReplyResponse.stream().forEach(e -> e.setGiveUpType(NO_GIVE_THE_THUMBS_UP));
        }else {
            for (CommentReplyResponse commentReplyRetou :commentReplyResponse) {
                for (ClUserComment clUserComment:giveUpComments) {
                    if (clUserComment.getCommentId().equals(commentReplyRetou.getCommentId())){
                        commentReplyRetou.setGiveUpType(GIVE_THE_THUMBS_UP);
                        break;
                    }else{
                        commentReplyRetou.setGiveUpType(NO_GIVE_THE_THUMBS_UP);
                    }
                }
            }
        }
        return commentReplyResponse;
    }

    @Override
    public List<CommentReplyResponse> findCommentReplyByCommentId(String commentId,String userId) {
        List<ClUserComment> giveUpComments;
        CommentReplyRequest commentReply = new CommentReplyRequest();
        commentReply.setCommentId(commentId);
        List<CommentReplyResponse> oneCommentReply = commentReplyMapper.findAllCommentReply(commentReply);
        giveUpComments = cUserCommentMapper.findGiveUpComments(userId);
        return this.addGiveType(oneCommentReply,giveUpComments);
    }

    @Override
    public List<CommentReplyResponse> findAllCommentReplyByCommentId(String commentId,String userId) {
        CommentReplyRequest commentReply = new CommentReplyRequest();
        List<ClUserComment> giveUpComments;
        commentReply.setPpCommentId(commentId);
        List<CommentReplyResponse> allCommentReply = commentReplyMapper.findAllCommentReply(commentReply);
        giveUpComments  = cUserCommentMapper.findGiveUpComments(userId);
        List<CommentReplyResponse> commentReplyResponses = this.addGiveType(allCommentReply, giveUpComments);
        for (int i = 0; i < commentReplyResponses.size() ; i++) {
            if (commentReplyResponses.get(i).getCommentId().equals(commentId)){
                commentReplyResponses.remove(i);
            }
        }
        return commentReplyResponses;
    }

    @Override
    public int findCommentsReplyNumber(String userId) {
        return commentReplyMapper.findAllCommentReplyByAlreadyRead(userId);
    }

    @Override
    public int findUserOpinionSubmitNumber(String userId) {
        Map<String,Object> map = new HashedMap();
        map.put("userId",userId);
        map.put("replyState",UNREAD_READ);
        map.put("state", ClUserOpinionVo.PROCESSED);
        return clUserOpinionMapper.findClUserOpinionNumber(map);
    }

    @Override
    @Transactional
    public CommentReply addNewCommentReply(CommentReplyRequest commentReplyRequest) throws UnsupportedEncodingException {
        CommentReply commentReply = new CommentReply();
        BeanUtils.copyProperties(commentReplyRequest,commentReply);
        Map<String,Object> map = new HashMap<>();
        map.put("id",commentReplyRequest.getVideoId());
        map.put("opType",ADDITIONAL_COMMENTS);
        Integer videoType = commentReplyRequest.getVideoType();
        //当前视频类型   10为首页视频   20为小视频
        if (videoType != null && SAMLL_VIDEOS_TYPR == videoType) {
            smallVideosMapper.updateSmallVideosCount(map);
        }
        this.addCommentReply(commentReply);
        CommentReply save = null;
        if(REPLYTYPE_ONE==commentReplyRequest.getReplyType()){
            //2020年2月26日16:36:52  HYL  优化一级评论  当一级评论保存时  将名称和头像数据进行冗余
            Map<String,Object> commentReplyMap = new HashMap<>();
            commentReplyMap.put("id",commentReply.getFromUid());
            ClUserVo clUserVo = clUserMapper.findClUserList(commentReplyMap).get(0);
            if (clUserVo != null) {
                commentReply.setFromNickname(clUserVo.getName());
                commentReply.setFromThumbImg(clUserVo.getImgUrl());
            }
            //一级评论默认id 为0
            commentReply.setReplyId("0");
            commentReply.setPCommentId(commentReply.getCommentId());
            save = commentReplyDao.save(commentReply);
            return save;
        }else if (REPLYTYPE_TWO==commentReplyRequest.getReplyType()){
            commentReply.setPCommentId(commentReplyRequest.getPpCommentId());
            //如果为二级评论则获取user表中的name昵称，冗余字段的toNickName中
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("id",commentReplyRequest.getToFromUid());
            List<ClUserVo> clUserList = clUserService.findClUserList(userMap);
            commentReply.setToNickname(clUserList.get(0).getName());
            /*if(commentReplyRequest.getReplyId().equals(commentReplyRequest.getPpCommentId())){
                commentReplyDao.updateAddOneCommentsBypCommentId(commentReplyRequest.getPpCommentId());
            }else {
                commentReplyDao.updateAddOneComment(commentReplyRequest.getReplyId());
                commentReplyDao.updateAddOneCommentsBypCommentId(commentReplyRequest.getPpCommentId());
            }*/
            save = commentReplyDao.save(commentReply);
            return save;
        }
        return save;
    }

    //设置评论数据初始值
    public CommentReply addCommentReply(CommentReply commentReply){
        commentReply.setCreateTime(new Date());
        commentReply.setNickTime(new Date());
        commentReply.setLikeNum(Long.valueOf(0));
        commentReply.setReplyNum(Long.valueOf(0));
        commentReply.setIsAuthor(0);
        commentReply.setIsTop(0);
        commentReply.setIsHot(0);
        commentReply.setAlreadyRead(0);
        commentReply.setCommentId(UUID.randomUUID().toString().replaceAll("-",""));
        return commentReply;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVideosInitInfo(String ids, String videoType){
        List<Long> userIdList = new ArrayList<>();
        for (int i=minNum; i<maxNum; i++) {
            userIdList.add((long)i);
        }
        List<CommentTemplate> commentTemplateList = commentTemplateDao.findAllByState(1L);
        String[] strIds = ids.split(",");
        Random rand=new Random();
        for (int i=0; i<strIds.length; i++) {
            int num = rand.nextInt(16) + 5;
            Collections.shuffle(commentTemplateList);
            List<CommentTemplate> commentTemplates = commentTemplateList.stream().limit(num).collect(Collectors.toList());
            Collections.shuffle(userIdList);
            List<Long> userIds = userIdList.stream().limit(num).collect(Collectors.toList());
            List<CommentReply> batchComment = Lists.newArrayList();
            for (int j=0; j<num; j++) {
                CommentReply commentReply = packageCommentInfo(commentTemplates.get(j).getContent(),userIds.get(j),Long.valueOf(strIds[i]),Integer.valueOf(videoType));
                batchComment.add(commentReply);
            }
            try {
                //批量保存评论数据
                commentReplyDao.saveAll(batchComment);
                //更新first_videos的评论数
                Map<String,Object> map = new HashMap<>(2);
                map.put("ids",Long.valueOf(strIds[i]));
                map.put("updateNum",num);
                firstVideosMapper.updateCommentCount(map);
            } catch (Exception e){
                log.error("批量保存评论数据失败："+e.getMessage(),e);
            }
        }
        return strIds.length;
    }

    /**
     * 组装评论数据
     */
    private CommentReply packageCommentInfo(String content,Long fromUid,Long videoId,Integer videoType) {
        CommentReply commentReply = new CommentReply();
        commentReply.setContent(content);
        commentReply.setFromUid(fromUid);
        commentReply.setVideoId(videoId);
        commentReply.setVideoType(videoType);
        this.addCommentReply(commentReply);
        Map<String,Object> commentReplyMap = new HashMap<>();
        commentReplyMap.put("id",commentReply.getFromUid());
        ClUserVo clUserVo = clUserMapper.findClUserList(commentReplyMap).get(0);
        if (clUserVo != null) {
            commentReply.setFromNickname(clUserVo.getName());
            commentReply.setFromThumbImg(clUserVo.getImgUrl());
        }
        commentReply.setPCommentId(commentReply.getCommentId());
        commentReply.setReplyId("0");
        commentReply.setReplyType(1);
        commentReply.setVarifyStatus(1);
        return commentReply;
    }

    @Override
    public void updateCommentCount() {
        String video_sql = "select v.id,comment_count FROM first_videos v where v.state = 1 ";
        String countSQL = "select count(*) from first_videos where state = 1 ";
        Object o = dynamicQuery.nativeQueryObject(countSQL);
        int count = Integer.parseInt(o + "");
        int loop = 0;
        while (true) {
            String selectSql =  video_sql + " order by v.id desc limit " + loop + "," + 500;
            List<Videos161Vo> videoIdList = dynamicQuery.nativeQueryList(Videos161Vo.class,selectSql);
            if (videoIdList == null||videoIdList.size()==0) {
                break;
            }
            String ids = videoIdList.stream().map(vo -> vo.getId()+"").collect(Collectors.joining(","));
            String commentSql = "select video_id as id,count(id) as comment_count from comment_reply where video_type=10 and varify_status = 1 " +
                    " and video_id in(" + ids + ")"+
                    " GROUP BY video_id ";
            List<Videos161Vo> commentList = dynamicQuery.nativeQueryList(Videos161Vo.class,commentSql);
            Map<Long, Integer> commentMap = commentList.stream().collect(Collectors.toMap(Videos161Vo::getId, Videos161Vo::getCommentCount));
            Map<Long, Integer> videoUpdateMap = new HashMap<>();
            for (Videos161Vo vo : videoIdList) {
                Integer commentCount = commentMap.get(vo.getId()) == null ? 0 : commentMap.get(vo.getId());
                if (!commentCount.equals(vo.getCommentCount())) {
                    videoUpdateMap.put(vo.getId(),commentCount);
                }
            }
            if (videoUpdateMap != null && videoUpdateMap.size() > 0) {
                StringBuilder videoIds = new StringBuilder();
                for (Map.Entry<Long, Integer> updateMap : videoUpdateMap.entrySet()) {
                    Long videoId = updateMap.getKey();
                    if ("".equals(videoIds.toString())) {
                        videoIds = new StringBuilder(videoId.toString());
                    } else {
                        videoIds.append(",").append(videoId.toString());
                    }
                    Integer commentCount = updateMap.getValue() == null ? 0 : updateMap.getValue();
                    String updateSql = " update first_videos set comment_count =  " + commentCount + " where id = " + videoId;
                    log.info("更新视频："+updateSql);
                    firstVideosMapper.updateFirstVideosRealWeight(updateSql);
                }
                String json = "videoAdd" + RabbitMQConstant._MQ_ + videoIds;
                rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
                log.warn("视频变动更新至ES，videoIds = '{}'  options = '{}'", videoIds.toString(), "videoAdd");
            }
            if (loop >= count) {
                break;
            }
            loop += 500;
        }
    }

    @Transactional
    @Override
    public void updateReplyCount(Integer type) {
        if (type == 1) {
            List<CommentReplyResponse> commentList = commentReplyMapper.selectCommentInfo();
            for (CommentReplyResponse replyResponse : commentList) {
                if (replyResponse.getReplyType() == 1) {
                    int count = commentReplyMapper.selectReplyNumByPCommentId(replyResponse.getCommentId());
                    if (replyResponse.getReplyNum().intValue() != count) {
                        commentReplyDao.updateReplyNum(count, replyResponse.getId());
                    }
                } else {
                    int count = commentReplyMapper.selectReplyNumByReplyId(replyResponse.getCommentId());
                    if (replyResponse.getReplyNum().intValue() != count) {
                        commentReplyDao.updateReplyNum(count, replyResponse.getId());
                    }
                }
            }
        } else {
            List<CommentReplyCountVo> countVoList1 = commentReplyMapper.selectCommentCountInfo1();
            for (CommentReplyCountVo countVo : countVoList1) {
                CommentReply commentReply = commentReplyMapper.selectReplyNum(countVo.getCommentId());
                if (commentReply != null && countVo.getCountNum() != commentReply.getReplyNum().intValue()) {
                    commentReplyDao.updateReplyNum(countVo.getCountNum(), commentReply.getId());
                }
            }

            List<CommentReplyCountVo> countVoList2 = commentReplyMapper.selectCommentCountInfo2();
            for (CommentReplyCountVo countVo : countVoList2) {
                CommentReply commentReply = commentReplyMapper.selectReplyNum(countVo.getCommentId());
                if (commentReply != null && countVo.getCountNum() != commentReply.getReplyNum().intValue()) {
                    commentReplyDao.updateReplyNum(countVo.getCountNum(), commentReply.getId());
                }
            }
        }
    }

}
