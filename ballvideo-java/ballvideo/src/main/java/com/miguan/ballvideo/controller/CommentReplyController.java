package com.miguan.ballvideo.controller;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.common.util.RdPage;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.sensitive.SensitiveWordUtil;
import com.miguan.ballvideo.entity.ClUserComment;
import com.miguan.ballvideo.entity.CommentReply;
import com.miguan.ballvideo.entity.CommentReplyRequest;
import com.miguan.ballvideo.entity.CommentReplyResponse;
import com.miguan.ballvideo.mapper.WarnKeywordMapper;
import com.miguan.ballvideo.service.ClUserCommentService;
import com.miguan.ballvideo.service.CommentReplyService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.impl.CommentReplyServiceImpl;
import com.miguan.ballvideo.vo.CommentReplyRequestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(value="评论接口controller",tags={"评论接口"})
@Slf4j
@RequestMapping("/api/commentReply")
@RestController
public class CommentReplyController{

    @Autowired
    private CommentReplyService commentReplyService;

    @Autowired
    private ClUserCommentService clUserCommentService;

    @Resource
    private WarnKeywordMapper warnKeywordMapper;

    @Resource
    private RedisService redisService;

    /**
     * 添加评论
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "添加评论")
    @PostMapping("/addCommentReply")
    public ResultMap addReply(@ModelAttribute CommentReplyRequest commentReplyRequest){
        Map<String, Object> restMap = new HashMap<>();
        try{
            boolean result = SensitiveWordUtil.contains(commentReplyRequest.getContent());
            if (result){
                return ResultMap.error("当前评论带有敏感词汇");
            }else {
                commentReplyRequest.setVarifyStatus(0);
                CommentReply commentReply = commentReplyService.addNewCommentReply(commentReplyRequest);
                if(commentReply!=null){
                    //删除对应视频的缓存评论
                    String keys = CommentReplyServiceImpl.FIRST_COMMENT_REPLY + commentReplyRequest.getVideoId()+"*";
                    Set<String> set = redisService.keys(keys);
                    for (String key : set) {
                        redisService.del(key);
                    }
                    return ResultMap.success("保存成功");
                }
            }
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    /**
     * 查询评论
     * 1.根据视频查看评论：currentPage，pageSize，type（1）,userId,replyType（1）,videoId，videoType，sortOrder
     * 2.根据评论查看回复：currentPage，pageSize, type（3）,userId,replyType（2）,ppCommentId
     * 3.个人消息：currentPage，pageSize，type（2），toFromUid
     * @param
     * @param
     * @return
     */
    //@AccessLimit(seconds = 60,maxCount = 30)
    @ApiOperation(value = "查询评论/查询回复/我的消息")
    @PostMapping("/QueryCommentReplByDynamic")
    public ResultMap findAllCommentReply(@ModelAttribute CommentReplyRequestVo commentReplyRequestVo){
        // type类型为1的时候，根据视频id查询一级评论，2的时候查询其他人评论当前用户的评论，3的时候查一级评论下的所有二级评论，userId必传字段
        Map<String, Object> result = new HashMap<>();
        String userId = commentReplyRequestVo.getUserId();
        if (commentReplyRequestVo.getType() == 2 &&(StringUtils.isBlank(userId) || "0".equals(userId))) {
            result.put("page", null);
            result.put("data", null);
        } else {
            Page<CommentReplyResponse> allCommentReply = commentReplyService.findAllCommentReply(commentReplyRequestVo);
            result.put("page", new RdPage(allCommentReply));
            result.put("data", allCommentReply);
        }
        return ResultMap.success(result);
    }

    /**
     * 评论点赞
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "评论点赞")
    @PostMapping("/GiveUpComments")
    public ResultMap GiveUpComments(@ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try {
            int i = clUserCommentService.GiveUpComments(clUserComment);
            if(i>0){
                return ResultMap.success("message","点赞成功");
            }
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    @ApiOperation(value = "取消评论点赞")
    @PostMapping("/delCommentsGiveUp")
    public ResultMap delCommentsGiveUp(@ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try {
            int i = clUserCommentService.delCommentsGiveUp(clUserComment);
            if(i>0){
                return ResultMap.success("message","取消点赞成功");
            }
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    @ApiOperation(value = "通过评论id查询评论详情")
    @GetMapping("/QueryCommentReplyByCommentId")
    public ResultMap findAllCommentReplyByCommentId(@RequestParam("commentId") String commentId,@ApiParam("用户id")String userId){
        Map<String, Object> restMap = new HashMap<>();
        try {
            List<CommentReplyResponse> oneCommentReply = commentReplyService.findCommentReplyByCommentId(commentId,userId);
            List<CommentReplyResponse> allCommentReply = commentReplyService.findAllCommentReplyByCommentId(commentId,userId);
            Map<String, Object> result = new HashMap<>();
            result.put("oneData", oneCommentReply);
            result.put("allData", allCommentReply);
            return ResultMap.success(result);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    /**
     * 查询评论点赞
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "查询评论点赞")
    @GetMapping("/findGiveUpComments")
    public ResultMap findGiveUpComments(String userId){
        Map<String, Object> restMap = new HashMap<>();
        try {
            List<ClUserComment> giveUpComments = clUserCommentService.findGiveUpComments(userId);
            return ResultMap.success(giveUpComments);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    /**
     * 查看是否有人回复了用户,
     *  Add 新增用户反馈功能的实现（西柚1.3.0）
     *  2019年9月25日09:15:25
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "查看是否有人(官方)回复了用户")
    @PostMapping("/findCommentsReplyNumber")
    public ResultMap findCommentsReplyNumber(String userId){
        Map<String, Object> restMap = new HashMap<>();
        try {
            int commentsReplyNumber = commentReplyService.findCommentsReplyNumber(userId);
            int userOpinionSubmitNumber = commentReplyService.findUserOpinionSubmitNumber(userId);
            restMap.put("unreadReply",commentsReplyNumber);
            restMap.put("unreadMessage",userOpinionSubmitNumber);
            return ResultMap.success(restMap);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    @ApiOperation(value = "更新视频评论总数")
    @PostMapping("/updateCommentCount")
    public ResultMap updateCommentCount(){
        commentReplyService.updateCommentCount();
        return ResultMap.success();
    }

    @ApiOperation(value = "更新评论回复总数")
    @PostMapping("/updateReplyCount")
    public ResultMap updateReplyCount(@RequestParam Integer type){
        commentReplyService.updateReplyCount(type);
        return ResultMap.success();
    }

}