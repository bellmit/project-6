package com.miguan.laidian.controller;

import com.github.pagehelper.Page;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.RdPage;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.sensitive.SensitiveWordUtil;
import com.miguan.laidian.entity.ClUserComment;
import com.miguan.laidian.entity.CommentReply;
import com.miguan.laidian.entity.CommentReplyRequest;
import com.miguan.laidian.entity.CommentReplyResponse;
import com.miguan.laidian.service.ClUserCommentService;
import com.miguan.laidian.service.CommentReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "评论controller", tags = {"评论接口"})
@RequestMapping("/api/commentReply")
@RestController
public class CommentReplyController {

    public static final Logger logger = LoggerFactory.getLogger(CommentReplyController.class);

    @Autowired
    private CommentReplyService commentReplyService;

    @Autowired
    private ClUserCommentService clUserCommentService;

    /**
     * 添加评论
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "添加评论")
    @PostMapping("/addCommentReply")
    public ResultMap addReply(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                              @ModelAttribute CommentReplyRequest commentReplyRequest){
        Map<String, Object> restMap = new HashMap<>();
        try{
            boolean result = SensitiveWordUtil.contains(commentReplyRequest.getContent());
            if (result){
                return ResultMap.error("当前评论带有敏感词汇");
            }else {
                commentReplyRequest.setAppType(commomParams.getAppType());
                CommentReply commentReply = commentReplyService.addNewCommentReply(commentReplyRequest);
                if(commentReply!=null){
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
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "查询评论")
    @PostMapping("/QueryCommentReplByDynamic")
    public ResultMap findAllCommentReply(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                         @ModelAttribute CommentReplyRequest commentReplyRequest ,
                                         @ApiParam("类型")int type){
        // type类型为1的时候，根据视频id查询一级评论，2的时候查询其他人评论当前用户的评论，3的时候查一级评论下的所有二级评论
        commentReplyRequest.setAppType(commomParams.getAppType());
        int currentPage = commomParams.getCurrentPage();
        int pageSize = commomParams.getPageSize();
        String userId = commomParams.getUserId();
        Page<CommentReplyResponse> allCommentReply = commentReplyService.findAllCommentReply(commentReplyRequest, currentPage, pageSize,type, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("page", new RdPage(allCommentReply));
        result.put("data", allCommentReply);
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
    public ResultMap GiveUpComments(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                    @ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try {
            clUserComment.setAppType(commomParams.getAppType());
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
    public ResultMap delCommentsGiveUp(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                       @ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try {
            clUserComment.setAppType(commomParams.getAppType());
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
    public ResultMap findAllCommentReplyByCommentId(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                    @RequestParam("commentId") String commentId){
        Map<String, Object> restMap = new HashMap<>();
        try {
            String userId = commomParams.getUserId();
            String appType = commomParams.getAppType();
            List<CommentReplyResponse> oneCommentReply = commentReplyService.findCommentReplyByCommentId(commentId,userId,appType);
            List<CommentReplyResponse> allCommentReply = commentReplyService.findAllCommentReplyByCommentId(commentId,userId,appType);
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
    public ResultMap findGiveUpComments(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams){
        Map<String, Object> restMap = new HashMap<>();
        try {
            String userId = commomParams.getUserId();
            String appType = commomParams.getAppType();
            List<ClUserComment> giveUpComments = clUserCommentService.findGiveUpComments(userId, appType);
            return ResultMap.success(giveUpComments);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    /**
     * 查看是否有人回复了用户
     *
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "查看是否有人回复了用户")
    @PostMapping("/findCommentsReplyNumber")
    public ResultMap findCommentsReplyNumber(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams){
        Map<String, Object> restMap = new HashMap<>();
        try {
            String userId = commomParams.getUserId();
            String appType = commomParams.getAppType();
            String deviceId = commomParams.getDeviceId();
            int commentsReplyNumber = commentReplyService.findCommentsReplyNumber(userId, appType);
            int userOpinionSubmitNumber = commentReplyService.findUserOpinionSubmitNumber(deviceId);
            restMap.put("unreadReply",commentsReplyNumber);
            restMap.put("unreadMessage",userOpinionSubmitNumber);
            return ResultMap.success(restMap);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

}