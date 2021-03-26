package com.miguan.laidian.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.entity.ClUserComment;
import com.miguan.laidian.entity.CommentReply;
import com.miguan.laidian.entity.CommentReplyRequest;
import com.miguan.laidian.entity.CommentReplyResponse;
import com.miguan.laidian.mapper.ClUserCommentMapper;
import com.miguan.laidian.mapper.ClUserOpinionMapper;
import com.miguan.laidian.mapper.CommentReplyMapper;
import com.miguan.laidian.mapper.SmallVideoMapper;
import com.miguan.laidian.repositories.CommentReplyDao;
import com.miguan.laidian.service.CommentReplyService;
import com.miguan.laidian.vo.ClUserOpinionVo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 评论信息回复ServiceImpl
 * @author HYL
 * @date 2019-08-09
 **/
@Service
public class CommentReplyServiceImpl implements CommentReplyService {

    @Autowired
    CommentReplyDao commentReplyDao;

    @Resource
    CommentReplyMapper commentReplyMapper;

    @Resource
    ClUserCommentMapper cUserCommentMapper;

    @Resource
    SmallVideoMapper iOSVideosMapper;

    @Resource
    private ClUserOpinionMapper clUserOpinionMapper;

    //0 未读
    public static final String UNREAD_READ = "0";

    @Override
    @Transactional
    public Page<CommentReplyResponse> findAllCommentReply(CommentReplyRequest commentReply, int currentPage, int pageSize, int type, String userId) {
        // type类型为1的时候，根据视频id查询一级评论，2的时候查询其他人评论当前用户的评论，3的时候查一级评论下的所有二级评论
        Map<String,Object> map = new HashMap<>();
        List<ClUserComment> giveUpComments;
        List<CommentReplyResponse> commentReplyResponse = null;
        map.put("userId",userId);
        if(!("").equals(userId)){
            giveUpComments = cUserCommentMapper.findGiveUpComments(userId,commentReply.getAppType());
            if(type==1){
                commentReply.setReplyType(1);
                if(currentPage==1){
                    List<CommentReplyResponse> commentReplyResponseGiveUp = commentReplyMapper.findAllCommentReplyByGiveUp(commentReply);
                    PageHelper.startPage(currentPage,pageSize);
                    commentReplyResponse = commentReplyMapper.findAllCommentReplyByNoGiveUpNumber(commentReply);
                    commentReplyResponse.addAll(0,commentReplyResponseGiveUp);
                    return (Page<CommentReplyResponse>) this.addGiveType(commentReplyResponse,giveUpComments);
                }else {
                    commentReplyResponse = commentReplyMapper.findAllCommentReplyByNoGiveUpNumber(commentReply);
                    return (Page<CommentReplyResponse>) this.addGiveType(commentReplyResponse,giveUpComments);
                }
            }else if(type==2){
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findMessageCenter(commentReply);
                int i = commentReplyMapper.updateCommentReplyByPComment(commentReply.getToFromUid(),commentReply.getAppType());
                return (Page<CommentReplyResponse>) commentReplyResponse;
            }else if(type==3){
                commentReply.setReplyType(2);
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findAllCommentReplyTow(commentReply);
                return (Page<CommentReplyResponse>) this.addGiveType(commentReplyResponse,giveUpComments);
            }
            return (Page<CommentReplyResponse>) commentReplyResponse;
        }else {
            if(type==1){
                commentReply.setReplyType(1);
                if(currentPage==1){
                    List<CommentReplyResponse> commentReplyResponseGiveUp = commentReplyMapper.findAllCommentReplyByGiveUp(commentReply);
                    PageHelper.startPage(currentPage,pageSize);
                    commentReplyResponse = commentReplyMapper.findAllCommentReplyByNoGiveUpNumber(commentReply);
                    commentReplyResponse.addAll(0,commentReplyResponseGiveUp);
                    return (Page<CommentReplyResponse>) commentReplyResponse;
                }else {
                    commentReplyResponse = commentReplyMapper.findAllCommentReplyByNoGiveUpNumber(commentReply);
                    return (Page<CommentReplyResponse>) commentReplyResponse;
                }
            }else if(type==2){
                PageHelper.startPage(currentPage,pageSize);
                commentReplyResponse = commentReplyMapper.findMessageCenter(commentReply);
                int i = commentReplyMapper.updateCommentReplyByPComment(commentReply.getToFromUid(),commentReply.getAppType());
                return (Page<CommentReplyResponse>) commentReplyResponse;
            }else if(type==3){
                PageHelper.startPage(currentPage,pageSize);
                commentReply.setReplyType(2);
                commentReplyResponse = commentReplyMapper.findAllCommentReplyTow(commentReply);
                return (Page<CommentReplyResponse>) commentReplyResponse;
            }
        }
        return (Page<CommentReplyResponse>) commentReplyResponse;
    }

    public List<CommentReplyResponse> addGiveType(List<CommentReplyResponse> commentReplyResponse, List<ClUserComment> giveUpComments){
        if(giveUpComments.size()==0){
            for (CommentReplyResponse commentReplyRetou :commentReplyResponse) {
                commentReplyRetou.setGiveUpType("1");
            }
        }else {
            for (CommentReplyResponse commentReplyRetou :commentReplyResponse) {
                for (ClUserComment clUserComment:giveUpComments) {
                    if (clUserComment.getCommentId().equals(commentReplyRetou.getCommentId())){
                        commentReplyRetou.setGiveUpType("0");
                        break;
                    }else{
                        commentReplyRetou.setGiveUpType("1");
                    }
                }
            }
        }
        return commentReplyResponse;
    }

    @Override
    public List<CommentReplyResponse> findCommentReplyByCommentId(String commentId,String userId, String appType) {
        List<ClUserComment> giveUpComments;
        CommentReplyRequest commentReply = new CommentReplyRequest();
        commentReply.setCommentId(commentId);
        List<CommentReplyResponse> oneCommentReply = commentReplyMapper.findAllCommentReply(commentReply);
        giveUpComments = cUserCommentMapper.findGiveUpComments(userId,appType);
        return this.addGiveType(oneCommentReply,giveUpComments);
    }

    @Override
    public List<CommentReplyResponse> findAllCommentReplyByCommentId(String commentId,String userId, String appType) {
        CommentReplyRequest commentReply = new CommentReplyRequest();
        List<ClUserComment> giveUpComments;
        commentReply.setPpCommentId(commentId);
        commentReply.setAppType(appType);
        List<CommentReplyResponse> allCommentReply = commentReplyMapper.findAllCommentReplyTow(commentReply);
        giveUpComments  = cUserCommentMapper.findGiveUpComments(userId,appType);
        List<CommentReplyResponse> commentReplyResponses = this.addGiveType(allCommentReply, giveUpComments);
        for (int i = 0; i < commentReplyResponses.size() ; i++) {
           if (commentReplyResponses.get(i).getCommentId().equals(commentId)){
               commentReplyResponses.remove(i);
           }
        }
        return commentReplyResponses;
    }

    @Override
    public int findCommentsReplyNumber(String userId, String appType) {
        return commentReplyMapper.findAllCommentReplyByAlreadyRead(userId, appType);
    }

    @Override
    public int findUserOpinionSubmitNumber(String deviceId) {
        Map<String,Object> map = new HashedMap();
        map.put("deviceId",deviceId);
        map.put("replyState", UNREAD_READ);
        map.put("state", ClUserOpinionVo.PROCESSED);
        return clUserOpinionMapper.findClUserOpinionNumber(map);
    }

    @Override
    @Transactional
    public CommentReply addNewCommentReply(CommentReplyRequest commentReplyRequest) throws UnsupportedEncodingException {
        CommentReply save = null;
        Map<String,Object> map = new HashMap<>();
        CommentReply commentReply = new CommentReply();
        BeanUtils.copyProperties(commentReplyRequest,commentReply);
        map.put("id",commentReplyRequest.getVideoId());
        map.put("opType",60);
        iOSVideosMapper.updateIOSVideosCount(map);
        //smallVideosMapper.updateSmallVideosCount(map);
        this.addCommentReply(commentReply);
        if(commentReplyRequest.getReplyType()==1){
            commentReply.setReplyId("0");
            commentReply.setPCommentId(commentReply.getCommentId());
            save = commentReplyDao.save(commentReply);
            return save;
        }else if (commentReplyRequest.getReplyType()==2){
            commentReply.setPCommentId(commentReplyRequest.getPpCommentId());
            commentReplyDao.updateAddOneComment(commentReplyRequest.getReplyId());
            save = commentReplyDao.save(commentReply);
            return save;
        }
        return save;
    }


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
}
