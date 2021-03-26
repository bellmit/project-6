package com.miguan.ballvideo.service.impl;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.WebHotNewsUtil;
import com.miguan.ballvideo.entity.HotWord;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.mapper.HotWordVideoMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.HotWordJpaRepository;
import com.miguan.ballvideo.service.HotWordService;
import com.miguan.ballvideo.service.MarketAuditService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.vo.video.HotWordVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("hotWordService")
public class HotWordServiceImpl implements HotWordService {

    @Resource
    private HotWordJpaRepository hotWordJpaRepository;

    @Resource
    private RedisService redisService;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private HotWordVideoMapper hotWordVideoMapper;

    private static String url="https://tophub.today/n/Jb0vmloB1G";
    private static String className="al";
    private static int number=10;

    @Override
    public void getBaiduHotWord(String editor) {
        List<String> hotWortStrList = WebHotNewsUtil.getWebHotVideos(url, className, number);
        if (!hotWortStrList.isEmpty()) {
            List<HotWord> hotWordList = new ArrayList<>();
            for (String word : hotWortStrList) {
                HotWord hotWord = hotWordJpaRepository.findTopByContent(word);
                if (hotWord == null) {
                    hotWord = new HotWord();
                    hotWord.setUpdateDate(new Date());
                    hotWord.setCreateDate(new Date());
                    hotWord.setContent(word);
                    hotWord.setEditor(editor);
                    hotWordList.add(hotWord);
                } else {
                    hotWord.setUpdateDate(new Date());
                    hotWord.setEditor(editor);
                    hotWordList.add(hotWord);
                }
            }
            if (!hotWordList.isEmpty()) {
                try {
                    hotWordJpaRepository.saveAll(hotWordList);
                } catch (Exception e) {
                    log.error("百度热词保存失败。");
                    e.printStackTrace();
                }
            }
        }
    }

    //@Scheduled(cron = "0 0 */4 * * ?")
    public void getBaiduHotWord() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.HOT_WORD, RedisKeyConstant.HOT_WORD_SECONDS);
        if (redisLock.lock()) {
            getBaiduHotWord("admin");
            log.info("获取百度当日前10热词--定时任务启动成功！");
        }
    }

    @Override
    public List<String> findHotWordInfo(String channelId, String appVersion, String mobileType) {
        List<HotWord> hotWordList;
        String faChannelId = ChannelUtil.filter(channelId, mobileType);
        String queryHotWordIds = StringUtil.getIdString(faChannelId, "search_hot_search_word");
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
        if(marketAudit!=null && StringUtils.isNotBlank(marketAudit.getHotWords())) {
            List<Integer> hidenHotWordList = new ArrayList<>();
            List<String> queryHotWordList = new ArrayList<>();
            String[] hidenHotWords = marketAudit.getHotWords().split(",");
            if (queryHotWordIds != "") {
                getQueryHotWordList(queryHotWordIds, queryHotWordList, hidenHotWords);
                hotWordList = getHotWords(queryHotWordList);
            } else {
                for (int i = 0; i < hidenHotWords.length; i++) {
                    hidenHotWordList.add(Integer.parseInt(hidenHotWords[i]));
                }
                hotWordList = hotWordJpaRepository.findHotWordList(hidenHotWordList);
            }
        } else {
            if (queryHotWordIds != "") {
                List<String> queryHotWordList = Arrays.asList(queryHotWordIds.split(","));
                hotWordList = getHotWords(queryHotWordList);
            } else {
                hotWordList = hotWordJpaRepository.findHotWordInfo();
            }
        }
        if (hotWordList.isEmpty()) {
            return null;
        }
        List<String> resultList = hotWordList.stream().map(HotWord::getContent).collect(Collectors.toList());
        return resultList;
    }

    //获取未屏蔽的热词Ids
    private void getQueryHotWordList(String queryChannelIds, List<String> queryHotWordList, String[] hidenHotWords) {
        String[] queryChannelIdStr = queryChannelIds.split(",");
        for (int j=0;j<queryChannelIdStr.length;j++) {
            //是否可查询
            boolean flag = true;
            for (int i = 0; i < hidenHotWords.length; i++) {
                if (hidenHotWords[i].equals(queryChannelIdStr[j])) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                queryHotWordList.add(queryChannelIdStr[j]);
            }
        }
    }

    //按热词Ids查询热词并排序
    private List<HotWord> getHotWords(List<String> queryHotWordList) {
        List<HotWord> hotWordList = Lists.newArrayList();
        List<HotWord> hotWords = hotWordJpaRepository.findHotWordListNew(queryHotWordList);
        //按查询Ids顺序排序
        for (String str : queryHotWordList) {
            for (HotWord hotWord : hotWords) {
                if (str.equals(hotWord.getId().toString())) {
                    hotWordList.add(hotWord);
                    break;
                }
            }
        }
        return hotWordList;
    }

    @Override
    public void freshHotWordInfo() {
        String key = "findHotWordInfo::ballVideos:cacheAble:findHotWordInfo:";
        if (redisService.exits(key)) {
            redisService.delByByte(key);
        }
    }

    @Override
    public List<HotWordVideoVo> findHotWordVideoInfo(String title) {
        List<HotWordVideoVo> hotWordVideos = Lists.newArrayList();
        HotWord hotWord = hotWordJpaRepository.findHotWordByContent(title);
        if (hotWord != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("hotWordId", hotWord.getId());
            hotWordVideos = hotWordVideoMapper.queryHotWordVideoList(param);
        }
        return  hotWordVideos;
    }

}
