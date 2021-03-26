package com.miguan.report.task;

import com.miguan.report.common.enums.AppLaiDianPackageEnum;
import com.miguan.report.common.enums.AppVideoPackageEnum;
import com.miguan.report.common.enums.ClientEnum;
import com.miguan.report.dto.AdvertCodeJoinDto;
import com.miguan.report.entity.report.BannerDataFromAdv;
import com.miguan.report.repository.BannerDataFromAdvRepository;
import com.miguan.report.service.adv.AdvertCodeJoinService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码位定时任务
 */
@Slf4j
@Component
public class CodeSpaceTask {

    @Resource
    private AdvertCodeJoinService advertCodeJoinService;
    @Resource
    private BannerDataFromAdvRepository bannerDataFromAdvRepository;

    /**
     * 每天凌晨1点，将广告库中有关与代码位的数据，读取到报表库中
     */
    @Scheduled(cron = "0 30 1 * * ?")
    public void queryYesterDayDataOfCodeSpace4Insert2ReportDB() {
        log.info("将广告库中有关与代码位的数据，读取到报表库中的任务开始");
        List<AdvertCodeJoinDto> dataList = advertCodeJoinService.queryTaskData();
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        Date date = DateUtils.addDays(new Date(), -1);
        List<BannerDataFromAdv> entityList = new ArrayList<BannerDataFromAdv>();
        for (AdvertCodeJoinDto dto : dataList) {

            String appPackage = dto.getAppPackage();
            ClientEnum clientEnum = ClientEnum.getByAdvId(dto.getMobileType());
            //特殊处理
            if ("xld".equals(appPackage)) {
                appPackage = AppLaiDianPackageEnum.LAI_DIAN_ANDROID.getPackageName();
            }

            if (StringUtils.isBlank(appPackage) || clientEnum == null) {
                continue;
            }

            BannerDataFromAdv entity = new BannerDataFromAdv();
            entity.setDate(date);
            entity.setClientId(clientEnum.getId());
            entity.setAppPackage(appPackage);
            entity.setTotalName(dto.getTotalName());
            entity.setComputer(dto.getComputer());
            entity.setOptionValue(dto.getOptionValue());
            entity.setAdId(dto.getAdId());
            entity.setChannelType(dto.getChannelType());
            entity.setLadderPrice(dto.getLadderPrice());
            //设置实验组id
            entity.setAbTestId(dto.getAbTestId());
            //设置排序值
            entity.setSortValue(dto.getSortValue());

            String cutAppName = null;
            Integer appId = null;
            AppLaiDianPackageEnum appLaiDianPackageEnum = AppLaiDianPackageEnum.getByPackageNameAndClientId(appPackage, clientEnum.getId());
            if (appLaiDianPackageEnum == null) {
                AppVideoPackageEnum appVideoPackageEnum = AppVideoPackageEnum.getByPackageNameAndClientId(appPackage, clientEnum.getId());
                if (appVideoPackageEnum != null) {
                    cutAppName = appVideoPackageEnum.getAppEnum().getAppName();
                    appId = appVideoPackageEnum.getAppEnum().getId();
                }
            } else {
                cutAppName = appLaiDianPackageEnum.getAppEnum().getAppName();
                appId = appLaiDianPackageEnum.getAppEnum().getId();
            }

            if (StringUtils.isNotBlank(cutAppName)) {
                entity.setCutAppName(cutAppName);
                entity.setAppId(appId);
                entityList.add(entity);
            }

        }

        Map<String, List<BannerDataFromAdv>> map = entityList.stream().collect(Collectors.groupingBy(e -> new StringBuilder().append(e.getAppId()).append(e.getClientId()).append(e.getTotalName()).toString()));
        entityList.clear();
        //排序
        entityList = map.entrySet().stream().flatMap(e -> {
            List<BannerDataFromAdv> list = e.getValue();
            list.sort(Comparator.comparing(BannerDataFromAdv::getSortValue).reversed());
            int i = 1;
            for (BannerDataFromAdv dataVo : list) {
                //概率补充不参与重新排序
                if (1 == dataVo.getComputer().intValue()) {
                    dataVo.setSortValue(dataVo.getOptionValue());
                    continue;
                }
                dataVo.setSortValue(i++);
            }
            return list.stream();
        }).collect(Collectors.toList());
        bannerDataFromAdvRepository.saveAll(entityList);
        log.info("将广告库中有关与代码位的数据，读取到报表库中的任务结束");
    }
}
