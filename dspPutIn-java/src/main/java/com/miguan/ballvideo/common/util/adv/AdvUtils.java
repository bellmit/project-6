package com.miguan.ballvideo.common.util.adv;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;
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
        List<AdvertCodeVo> clone = Lists.newCopyOnWriteArrayList(advertVos);
        AdvertCodeVo firstAdv = computerGetOne(advertVos);
        List<AdvertCodeVo> olderThanProbability =
                clone.stream()
                        .filter(p -> p.getId().intValue()!=firstAdv.getId().intValue())
                        .sorted(Comparator.comparing(AdvertCodeVo::getOptionValue).reversed())
                        .collect(Collectors.toList());
        olderThanProbability.add(0,firstAdv);
        return olderThanProbability;
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
        }
        return null;
    }

    public static String filter(Map<String,Object> params){
        params.remove("deviceId");
        params.remove("catId");
        params.remove("videoType");
        params.remove("marketChannelId");
        return params.toString();
    }

}