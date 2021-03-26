package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.ClUserComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ClUserCommentDao extends JpaRepository<ClUserComment, Long> {


    @Modifying
    @Query(value = "delete from cl_user_comment cuc where cuc.user_id = ?1 and cuc.comment_id = ?2", nativeQuery = true)
    public int deleteByCommentIdandUserId(Long userId, String commentId);

}
