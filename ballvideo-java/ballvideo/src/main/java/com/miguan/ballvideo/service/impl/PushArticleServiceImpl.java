package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.repositories.PushArticleDao;
import com.miguan.ballvideo.service.PushArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 消息推送
 * Created by shixh on 2019/9/10.
 *
 */
@Slf4j
@Service
public class PushArticleServiceImpl implements PushArticleService {

    public final int pushType = 1; //1 定时 2立即推送

    public final int pushType3 = 3; //3 垃圾清理

    @Resource
    private PushArticleDao pushArticleDao;

    @Override
    public PushArticle getOneToPush() {
        List<PushArticle> pushArticles = pushArticleDao.findByState(Constant.open);
        if(CollectionUtils.isEmpty(pushArticles))return null;
        int nums = pushArticles.size();
        int index = getRandomBetween(0,nums);
        return pushArticles.get(index);
    }

    @Override
    public PushArticle findOneToPush(Long id) {
        Optional<PushArticle> optional = pushArticleDao.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public List<PushArticle> findByType(Integer type) {
        List<PushArticle> pushArticles = pushArticleDao.findByTypeAndState(type, 1);
        if(CollectionUtils.isEmpty(pushArticles))return null;
        return pushArticles;
    }

    public static int getRandomBetween(int first,int second){
        return (int)((second-first)* Math.random()+first);
    }

    @Override
    public List<PushArticle> findFixedTimeListToPush() {
        List<PushArticle> fixedTimeList = new ArrayList<>();
        List<PushArticle> list = pushArticleDao.findAllByStateAndPushType(Constant.open, pushType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        for (PushArticle pushArticle : list) {
            String pushTime = pushArticle.getPushTime();
            if (StringUtils.isEmpty(pushTime)){
                continue;
            }
            //推送时间大于当前时间
            Date parse = null;
            try {
                parse = sdf.parse(pushTime);
            } catch (ParseException e) {
               log.error("[{}],时间转换异常", pushTime);
            }
            if (parse.after(date)){
                fixedTimeList.add(pushArticle);
            }
        }
        return fixedTimeList;
    }

    @Override
    public List<PushArticle> cleanPushInfo() {
        List<PushArticle> list = pushArticleDao.findAllByStateAndPushType(Constant.open, pushType3);
        return list;
    }
}