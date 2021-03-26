package com.miguan.xuanyuan.common.util.adv;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.dto.AdCodeDto;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shixh
 * @Date 2020/4/2
 **/
public class AdvUtils {

  /**
   * 广告概率算法（V2.5以上版本用到）
   * 1、根据概率算出一个广告排第一个位置；
   * 2、剩余广告根据概率排序展示；
   * @param advertVos
   * @return
   */
  public static List<AdCodeDto> computerAndSort(List<AdCodeDto> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        if(advertVos.size()==1)return advertVos;
        List<AdCodeDto> sortResultList = Lists.newArrayList();
        //广告配比排序（走AB实验）：1、相同序号根据概率算出一个广告；2、剩余广告根据序号正序排序展示；
        Map<Integer, List<AdCodeDto>> mapList = advertVos.stream().collect(Collectors.groupingBy(AdCodeDto::getOptionValue));
        for(Map.Entry<Integer, List<AdCodeDto>> mapEntry : mapList.entrySet()) {
            List<AdCodeDto> codeResultVoList = mapEntry.getValue();
            if (codeResultVoList.size() > 1) {
                AdCodeDto codeVo = computerGetOne(codeResultVoList);
                sortResultList.add(codeVo);
            } else {
                sortResultList.addAll(codeResultVoList);
            }
        }
        sortResultList.sort(Comparator.comparing(AdCodeDto::getOptionValue));
        return sortResultList;
    }

    public static AdCodeDto computerGetOne(List<AdCodeDto> advertVoList) {
        if(CollectionUtils.isEmpty(advertVoList))return null;
        int maxRateNum = 0;
        List<Integer> rateLengths = Lists.newArrayList();
        for (AdCodeDto advertDto : advertVoList) {
            maxRateNum += advertDto.getRateNum();
            rateLengths.add(maxRateNum);
        }
        if(maxRateNum==0)return advertVoList.get(0);//存在概率都为0的数据
        Random random = new Random();
        int a = random.nextInt(maxRateNum) + 1;
        for (int i = 0 ; i < rateLengths.size() ; i++) {
            if(a <= rateLengths.get(i)){
                return advertVoList.get(i);
            }
        }
        return advertVoList.get(0);
    }


  /** 根据后台序号排序广告
   * @param advertVos
   * @return
   */
  public static List<AdCodeDto> sort(List<AdCodeDto> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        List<AdCodeDto> olderThanSort =
                advertVos.stream()
                        .sorted(Comparator.comparing(AdCodeDto::getOptionValue).reversed())
                        .collect(Collectors.toList());
        return olderThanSort;
    }

  /**
   * 相同广告位置的广告，通过算法排序
   * @param advertCodeDtos
   * @return
   */
  public static List<AdCodeDto> sortByComputer(List<AdCodeDto> advertCodeDtos) {
        if(CollectionUtils.isEmpty(advertCodeDtos))return null;
        int computer = advertCodeDtos.get(0).getSortType();
        if (computer == 1) {
            return computerAndSort(advertCodeDtos);
        }else if(computer == 2){
            return sort(advertCodeDtos);
        }else if(computer == 3){
            //自动排序
            return sort(advertCodeDtos);
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