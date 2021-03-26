package com.miguan.laidian.service;


import com.miguan.laidian.entity.ClUserComment;

public interface VoteService {

    ClUserComment addVoteUserComment(ClUserComment clUserComment);

    int deleteVoteUserComment(ClUserComment clUserComment);
}
