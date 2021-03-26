package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.ClUserComment;

import java.util.List;

public interface ClUserCommentService {
    /**
    * @Description:保存用户点赞
    * @Param: clUserComment
    * @return: ClUserComment
    * @Author: hyl
    * @Date: 2019年9月11日10:26:32
    */
    int GiveUpComments(ClUserComment clUserComment);

    /**
     * @Description: 查询用户评论点赞
     * @Param: userId
     * @return: ClUserComment
     * @Author: hyl
     * @Date: 2019年9月11日10:26:35
     */
    List<ClUserComment> findGiveUpComments(String userId);


    /**
     * @Description: 删除用户评论点赞
     * @Param: userId
     * @return: ClUserComment
     * @Author: hyl
     * @Date: 2019年9月11日10:26:38
     */
    int delCommentsGiveUp(ClUserComment clUserComment);
}
