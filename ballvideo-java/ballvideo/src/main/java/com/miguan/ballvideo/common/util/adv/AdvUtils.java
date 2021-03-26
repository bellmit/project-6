package com.miguan.ballvideo.common.util.adv;

import com.cgcg.context.SpringContextHolder;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shixh
 * @Date 2020/4/2
 **/
public class AdvUtils {

    public static BannerPriceLadderVo ladderAdvIsOpen(String positionType, String mobileType, String appVersion, String appPackage) {
        Iterator it = AdvGlobal.priceLadderMap.get(positionType + "_" + mobileType + "_" + appPackage).iterator();
        List<BannerPriceLadderVo> nannerPriceLadderVos = Lists.newArrayList(it);
        for (BannerPriceLadderVo nannerPriceLadderVo : nannerPriceLadderVos) {
            if (VersionUtil.isBetween(nannerPriceLadderVo.getVersionStart(), nannerPriceLadderVo.getVersionEnd(), appVersion))
                return nannerPriceLadderVo;
        }
        return null;
    }

    public static List<AdvertVo> random(List<AdvertVo> advs, int num) {
        if (CollectionUtils.isEmpty(advs)) return Lists.newArrayList();
        if (advs.size() <= num) return advs;
        Collections.shuffle(advs);
        return advs.subList(0, num);
    }

    /**
     * 根据展示概率获取广告
     *
     * @param advertVoList
     * @param count
     * @return
     */
    public static List<AdvertVo> computer(List<AdvertVo> advertVoList, int count) {
        if(CollectionUtils.isEmpty(advertVoList))return null;
        List<AdvertVo> result = new ArrayList<>();
        List<AdvertVo> advertVoTempList = new ArrayList<>();
        for (AdvertVo advertVo : advertVoList) {
            int probability = advertVo.getProbability();
            for (int i = 0; i < probability; i++) {
                advertVoTempList.add(advertVo);
            }
        }
        if(CollectionUtils.isEmpty(advertVoTempList))return advertVoTempList;
        Random random = new Random();
        for (int i = 1; i <= count; i++) {
            int a = random.nextInt(advertVoTempList.size());
            result.add(advertVoTempList.get(a));
        }
        return result;
    }

  /**
   * 不同LinkType各返回1条
   * @param datas
   * @return
   */
  public static List<AdvertVo> splitByLinkType(List<AdvertVo> datas) {
        List<AdvertVo> results = Lists.newArrayList();
        Map<String, List<AdvertVo>> linkType_advers = datas.stream().collect(Collectors.groupingBy(t -> t.getLinkType()));
        for (Map.Entry<String, List<AdvertVo>> map : linkType_advers.entrySet()) {
            results.add(map.getValue().get(0));
        }
        return results;
    }

  /**
   * 广告概率算法（V2.5以上版本用到）
   * 1、根据概率算出一个广告排第一个位置；
   * 2、剩余广告根据概率排序展示；
   * @param advertVos
   * @return
   */
  public static List<AdvertCodeVo> computerAndSort(List<AdvertCodeVo> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        if(advertVos.size()==1)return advertVos;
        List<AdvertCodeVo> sortResultList = Lists.newArrayList();
        Integer testFlag = advertVos.get(0).getTestFlag();
        if (testFlag != null && advertVos.get(0).getSortNumber() != null) {
            //广告配比排序（走AB实验）：1、相同序号根据概率算出一个广告；2、剩余广告根据序号正序排序展示；
            Map<Integer, List<AdvertCodeVo>> mapList = advertVos.stream().collect(Collectors.groupingBy(AdvertCodeVo::getSortNumber));
            for(Map.Entry<Integer, List<AdvertCodeVo>> mapEntry : mapList.entrySet()) {
                List<AdvertCodeVo> codeResultVoList = mapEntry.getValue();
                if (codeResultVoList.size() > 1) {
                    AdvertCodeVo codeVo = computerGetOne(codeResultVoList);
                    sortResultList.add(codeVo);
                } else {
                    sortResultList.addAll(codeResultVoList);
                }
            }
            sortResultList.sort(Comparator.comparing(AdvertCodeVo::getSortNumber));
        } else {
            List<AdvertCodeVo> clone = Lists.newCopyOnWriteArrayList(advertVos);
            AdvertCodeVo firstAdv = computerGetOne(advertVos);
            sortResultList =
                    clone.stream()
                            .filter(p -> p.getId().intValue() != firstAdv.getId().intValue())
                            .sorted(Comparator.comparing(AdvertCodeVo::getOptionValue).reversed())
                            .collect(Collectors.toList());
            sortResultList.add(0, firstAdv);
        }
        return sortResultList;
    }

    public static AdvertCodeVo computerGetOne(List<AdvertCodeVo> advertVoList) {
        if(CollectionUtils.isEmpty(advertVoList))return null;
        List<AdvertCodeVo> advertVoTempList = new ArrayList<>();
        for (AdvertCodeVo advertVo : advertVoList) {
            int probability = advertVo.getOptionValue();
            for (int i = 0; i < probability; i++) {
                advertVoTempList.add(advertVo);
            }
        }
        if(advertVoTempList.size()==0)return advertVoList.get(0);//存在概率都为0的数据
        Random random = new Random();
        int a = random.nextInt(advertVoTempList.size());
        return advertVoTempList.get(a);
    }

  /** 根据后台序号排序广告
   * @param advertVos
   * @return
   */
  public static List<AdvertCodeVo> sort(List<AdvertCodeVo> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        List<AdvertCodeVo> olderThanSort =
                advertVos.stream()
                        .sorted(Comparator.comparing(AdvertCodeVo::getOptionValue).reversed())
                        .collect(Collectors.toList());
        return olderThanSort;
    }

  /**
   * 相同广告位置的广告，通过算法排序
   * @param advertCodeVos
   * @return
   */
  public static List<AdvertCodeVo> sortByComputer(List<AdvertCodeVo> advertCodeVos) {
        if(CollectionUtils.isEmpty(advertCodeVos))return null;
        int computer = advertCodeVos.get(0).getComputer();
        if (computer == 1) {
            return computerAndSort(advertCodeVos);
        }else if(computer == 2){
            return sort(advertCodeVos);
        }else if(computer == 3){
            //自动排序
            return sort(advertCodeVos);
        } else {
            return sort(advertCodeVos);
        }
    }

    public static String filter(Map<String,Object> params){
        params.remove("deviceId");
        params.remove("catId");
        params.remove("videoType");
        params.remove("marketChannelId");
        return params.toString();
    }

    public static Map<String,Object> getIncentiveInfo(String mobileType, String appPackage, AbTestAdvParamsVo queueVo,int defaultValue) {
        Map<String,Object> resultMap = new HashMap<>(3);
        Map<String, Object> advParam = new HashMap<>();
        advParam.put("queryType","position");
        advParam.put("positionType", "incentiveVideoPosition");
        advParam.put("mobileType", mobileType);
        advParam.put("appPackage", appPackage);
        AdvertService advertService = SpringContextHolder.getBean("advertService");
        List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(queueVo, advParam, 3);
        if (CollectionUtils.isNotEmpty(list)) {
            Integer isShowIncentive = list.get(0).getMaxShowNum() == null ? defaultValue : list.get(0).getMaxShowNum();
            Integer position = list.get(0).getFirstLoadPosition() == null ? defaultValue : list.get(0).getFirstLoadPosition();
            String incentiveVideoRate = list.get(0).getSecondLoadPosition() == null ? "0" : list.get(0).getSecondLoadPosition() + "";
            resultMap.put("isShowIncentive",isShowIncentive);
            resultMap.put("position",position);
            resultMap.put("incentiveVideoRate",incentiveVideoRate);
        }
        return resultMap;
    }

}