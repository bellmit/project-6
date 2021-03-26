package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.ClUserComment;
import com.miguan.ballvideo.service.VoteService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "用户评论-点赞/取消点赞接口ApiController",tags={"用户评论-点赞/取消点赞接口"})
@RequestMapping("/vote")
@RestController
public class VoteController {

    @Autowired
    private VoteService voteService;

    @GetMapping("/addVoteUserComment")
    public ResultMap addVoteUserComment(@ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try{
            voteService.addVoteUserComment(clUserComment);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }

    @GetMapping("/deleteVoteUserComment")
    public ResultMap deleteVoteUserComment(@ModelAttribute ClUserComment clUserComment){
        Map<String, Object> restMap = new HashMap<>();
        try{
            voteService.deleteVoteUserComment(clUserComment);
        }catch (Exception e){
            ResultMap.error(restMap);
        }
        return ResultMap.success(restMap);
    }


}
