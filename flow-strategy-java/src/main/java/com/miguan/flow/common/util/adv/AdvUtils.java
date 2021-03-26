package com.miguan.flow.common.util.adv;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.miguan.flow.dto.AdvertCodeDto;

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
  public static List<AdvertCodeDto> computerAndSort(List<AdvertCodeDto> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        if(advertVos.size()==1)return advertVos;
        List<AdvertCodeDto> sortResultList = Lists.newArrayList();
        Integer testFlag = advertVos.get(0).getTestFlag();
        if (testFlag != null && advertVos.get(0).getSortNumber() != null) {
            //广告配比排序（走AB实验）：1、相同序号根据概率算出一个广告；2、剩余广告根据序号正序排序展示；
            Map<Integer, List<AdvertCodeDto>> mapList = advertVos.stream().collect(Collectors.groupingBy(AdvertCodeDto::getSortNumber));
            for(Map.Entry<Integer, List<AdvertCodeDto>> mapEntry : mapList.entrySet()) {
                List<AdvertCodeDto> codeResultVoList = mapEntry.getValue();
                if (codeResultVoList.size() > 1) {
                    AdvertCodeDto codeVo = computerGetOne(codeResultVoList);
                    sortResultList.add(codeVo);
                } else {
                    sortResultList.addAll(codeResultVoList);
                }
            }
            sortResultList.sort(Comparator.comparing(AdvertCodeDto::getSortNumber));
        } else {
            List<AdvertCodeDto> clone = Lists.newCopyOnWriteArrayList(advertVos);
            AdvertCodeDto firstAdv = computerGetOne(advertVos);
            sortResultList =
                    clone.stream()
                            .filter(p -> p.getId().intValue() != firstAdv.getId().intValue())
                            .sorted(Comparator.comparing(AdvertCodeDto::getOptionValue).reversed())
                            .collect(Collectors.toList());
            sortResultList.add(0, firstAdv);
        }
        return sortResultList;
    }

    public static AdvertCodeDto computerGetOne(List<AdvertCodeDto> advertVoList) {
        if(CollectionUtils.isEmpty(advertVoList))return null;
        List<AdvertCodeDto> advertVoTempList = new ArrayList<>();
        for (AdvertCodeDto advertDto : advertVoList) {
            int probability = advertDto.getOptionValue();
            for (int i = 0; i < probability; i++) {
                advertVoTempList.add(advertDto);
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
  public static List<AdvertCodeDto> sort(List<AdvertCodeDto> advertVos) {
        if(CollectionUtils.isEmpty(advertVos))return null;
        List<AdvertCodeDto> olderThanSort =
                advertVos.stream()
                        .sorted(Comparator.comparing(AdvertCodeDto::getOptionValue).reversed())
                        .collect(Collectors.toList());
        return olderThanSort;
    }

  /**
   * 相同广告位置的广告，通过算法排序
   * @param advertCodeDtos
   * @return
   */
  public static List<AdvertCodeDto> sortByComputer(List<AdvertCodeDto> advertCodeDtos) {
        if(CollectionUtils.isEmpty(advertCodeDtos))return null;
        int computer = advertCodeDtos.get(0).getComputer();
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