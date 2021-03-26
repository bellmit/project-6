package com.miguan.laidian.service.impl;


import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.repositories.PushArticleDao;
import com.miguan.laidian.service.PushArticleService;
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
 */
@Slf4j
@Service
public class PushArticleServiceImpl implements PushArticleService {

    public static  final  int pushType = 1; //1 定时 2立即推送

    @Resource
    private PushArticleDao pushArticleDao;

    @Override
    public PushArticle findOneToPush(Long id) {
        PushArticle pushArticles = pushArticleDao.getOne(id);
        return pushArticles;
    }

    @Override
    public PushArticle findOne(Long id) {
        Optional<PushArticle> optional = pushArticleDao.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public List<PushArticle> findAllFixedTimeListToPush() {
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
}