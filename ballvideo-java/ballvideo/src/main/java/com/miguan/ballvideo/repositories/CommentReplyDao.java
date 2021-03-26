package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentReplyDao extends JpaRepository<CommentReply, Long> {

    @Modifying
    @Query(value = "update comment_reply com set com.like_num = com.like_num + 1 where com.comment_id = ?1",nativeQuery = true)
    public int updateGiveUpComment(String commentId);

    @Modifying
    @Query(value = "update comment_reply com set com.like_num = com.like_num - 1 where com.comment_id = ?1",nativeQuery = true)
    public int deleteGiveUpComment(String commentId);

    @Modifying
    @Query(value = "update comment_reply com set com.reply_num = ?1 where com.id = ?2",nativeQuery = true)
    int updateReplyNum(Integer replyNum,Long id);

}
