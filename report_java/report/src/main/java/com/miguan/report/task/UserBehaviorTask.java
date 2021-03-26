package com.miguan.report.task;

import com.miguan.report.common.config.mongo.SecondMongoConfig;
import com.miguan.report.common.enums.ShenceAppKeyEnum;
import com.miguan.report.common.type.Alias;
import com.miguan.report.common.util.AppNameUtil;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.entity.BannerDataUserBehavior;
import com.miguan.report.mapper.ImportMapper;
import com.miguan.report.vo.UserBehaviorVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import static com.miguan.report.common.util.DateUtil.dateToStr;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * 用户行为分析
 */
@Slf4j
@Component
public class UserBehaviorTask {

    @Resource
    @Qualifier(SecondMongoConfig.MONGO_TEMPLATE)
    private MongoTemplate secMongoTemplate;
    @Resource
    private ImportMapper importMapper;

    /**
     * 每天凌晨1点，将广告库中有关与代码位的数据，读取到报表库中
     */
    @Scheduled(cron = "${task.scheduled.cron.user-behavior}")
    public void doWork() {
        String day = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        doWork(day);
    }

    public void doWork(String date) {
        Date dateO = DateUtil.strToDate(date, "yyyyMMdd");
        Date dateC = new Date();
        log.info("用户行为分析的任务开始；日期 ==> {}", date);
        List<BannerDataUserBehavior> all = new ArrayList<>();

        //查询日活
        GroupOperation gourp1 = group("app_package", "system_version", "device_id");
        GroupOperation gourp2 = group(Fields.from(Fields.field("app_package", "$_id.app_package")).and("system_version", "$_id.system_version"))
                .count().as("total");
        List<UserBehaviorVo> activeVos = doAgg(Aggregation.newAggregation(gourp1, gourp2), date);
        Map<String, LongSummaryStatistics> active = activeVos.stream().collect(Collectors.groupingBy(e -> {
            return e.get_id().getApp_package();
        }, Collectors.summarizingLong(UserBehaviorVo::getTotal)));

        //启动页	人均启动次数	action_id=app_start/日活用户数
        MatchOperation match = match(Criteria.where("action_id").is("App_start"));
        GroupOperation group = group("app_package", "system_version")
                .count().as("total");
        Aggregation agg = Aggregation.newAggregation(match, group);
        buildEntity(active, dateO, dateC, "启动页", doAgg(agg, date), all);

        //小视频列表	人均小视频菜单栏点击用户数	action_id=Icon_click,icon_type=20/日活用户数
        match = match(Criteria.where("action_id").is("Icon_click").and("icon_type").is(20));
        agg = Aggregation.newAggregation(match, group);
        buildEntity(active, dateO, dateC, "小视频列表", doAgg(agg, date), all);

        //小视频详情	人均观看视频数（类型是小视频）	action_id=xy_video_playover（type=20）/日活用户数
        match = match(Criteria.where("action_id").is("xy_video_playover").and("type").is(20));
        agg = Aggregation.newAggregation(match, group);
        buildEntity(active, dateO, dateC, "小视频详情", doAgg(agg, date), all);

        //视频底部banner广告	2.3版本以上用户人均首页列表观看视频数	action_id=xy_video_playover（type=10 or is null，source=10,11,12、app_version在2.3.0版本以上）/日活用户数
        ConditionalOperators.Switch switch1 = buildSwitch(BooleanOperators.And.and(
                ArrayOperators.In.arrayOf(Arrays.asList(10, 11, 12)).containsValue("$source"),
                ComparisonOperators.valueOf("app_version").greaterThanEqualToValue("2.3.0")));
        //视频结束广告	人均观看视频完播数	action_id=xy_video_playover（type=10 or is null）、video_rate=100/日活用户数
        ConditionalOperators.Switch switch2 = buildSwitch(ComparisonOperators.valueOf("video_rate").equalToValue(100));
        //视频中间广告	人均视频观看35%以上视频数	action_id=xy_video_playover（type=10 or is null）、video_rate>=35/日活用户数
        ComparisonOperators.Gte switch3 = ComparisonOperators.valueOf("video_rate").greaterThanEqualToValue(35);
        //首页列表	人均观看视频数（来源是首页列表）	action_id=xy_video_playover（type=10 or is null，source=10,11,12)/日活用户数
        ConditionalOperators.Switch switch4 = buildSwitch(ArrayOperators.In.arrayOf(Arrays.asList(10, 11, 12)).containsValue("$source"));
        //首页视频详情	人均观看视频数（来源是详情）	action_id=xy_video_playover（type=10 or is null，source=20）/日活用户数
        ConditionalOperators.Switch switch5 = buildSwitch(ComparisonOperators.valueOf("source").equalToValue(20));
        //搜索结果广告	人均观看视频数（来源是搜索结果）	action_id=xy_video_playover（type=10 or is null，source=63）/日活用户数
        ConditionalOperators.Switch switch6 = buildSwitch(ComparisonOperators.valueOf("source").equalToValue(63));
        //搜索页广告	人均观看视频数（来源是搜索页）	action_id=xy_video_playover（type=10 or is null，source=62）/日活用户数
        ConditionalOperators.Switch switch7 = buildSwitch(ComparisonOperators.valueOf("source").equalToValue(62));
        //锁屏原生广告	人均观看视频数（来源是锁屏列表）	action_id=xy_video_playover（type=10 or is null，source=30）/日活用户数
        ConditionalOperators.Switch switch8 = buildSwitch(ComparisonOperators.valueOf("source").equalToValue(30));

        ProjectionOperation project = project().andInclude("app_package", "system_version")
                .and(switch1).as("sum1")
                .and(switch2).as("sum2")
                .and(switch3).as("sum3")
                .and(switch4).as("sum4")
                .and(switch5).as("sum5")
                .and(switch6).as("sum6")
                .and(switch7).as("sum7")
                .and(switch8).as("sum8");

        match = match(Criteria.where("action_id").is("xy_video_playover")
                .orOperator(Criteria.where("type").exists(false), Criteria.where("type").is(10), Criteria.where("type").is(null)));
        group = group("app_package", "system_version")
                //视频开始广告	人均观看视频数	action_id=xy_video_playover（type=10 or is null）/日活用户数
                .count().as("total0")
                .sum("sum1").as("total1")
                .sum("sum2").as("total2")
                .sum("sum3").as("total3")
                .sum("sum4").as("total4")
                .sum("sum5").as("total5")
                .sum("sum6").as("total6")
                .sum("sum7").as("total7")
                .sum("sum8").as("total8");
        agg = Aggregation.newAggregation(match, project, group);
        buildEntity(active, dateO, dateC, "AT", doAgg(agg, date), all);
        all = all.stream().filter(e -> e != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(all)) {
            log.warn("data='{}' 此次同步没有获取到任何数据", date);
            return;
        }
        importMapper.deleteBannerDataUserBehaviorByDate(dateToStr(dateO, "yyyy-MM-dd"));
        importMapper.addUserBehavior(all);
        log.info("用户行为分析的任务结束");
    }

    /**
     * 创建数据库用户行为实体
     * @param adSpace
     * @param list
     * @param all
     */
    private void buildEntity(Map<String, LongSummaryStatistics> active, Date date, Date cdate, String adSpace, List<UserBehaviorVo> list, List<BannerDataUserBehavior> all) {
        List<BannerDataUserBehavior> r = null;
        if ("AT".equals(adSpace)) {
            r = list.stream().flatMap(e -> {
                List<BannerDataUserBehavior> tmp = new ArrayList<>();
                String prefs = "total";
                try {
                    for (int i = 0; i <= 8; i++) {
                        Field file = FieldUtils.getField(UserBehaviorVo.class, prefs + i, true);
                        long total = file.getLong(e);
                        Alias alias = file.getAnnotation(Alias.class);
                        String adSpaceS = alias.value();
                        tmp.add(findEntityType(active, e.get_id().getApp_package(), date, cdate, adSpaceS, total));
                    }
                } catch (IllegalAccessException ex) {
                    log.error("JAVA反射出现异常=>{}", ex.getMessage(), ex);
                }
                return tmp.stream();
            }).collect(Collectors.toList());
        } else {
            r = list.stream().map(e -> findEntityType(active, e.get_id().getApp_package(), date, cdate, adSpace, e.getTotal())).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(r)) {
            all.addAll(r);
        }
    }


    private BannerDataUserBehavior findEntityType(Map<String, LongSummaryStatistics> active, String appPackage, Date date, Date cdate, String adSpace, long total) {
        for (ShenceAppKeyEnum value : ShenceAppKeyEnum.values()) {
            if (value.getCode().equals(appPackage)) {
                LongSummaryStatistics activeNum = active.get(appPackage);
                return buildEntity(Long.valueOf(activeNum.getSum()).intValue(), date, cdate, adSpace, value, total);
            }
        }
        return null;
    }

    /**
     * 构造数据库实体
     * @param date
     * @param cdate
     * @param adSpace
     * @param value
     * @param total
     * @return
     */
    private BannerDataUserBehavior buildEntity(int active, Date date, Date cdate, String adSpace, ShenceAppKeyEnum value, long total) {
        BannerDataUserBehavior bannerDataUserBehavior = new BannerDataUserBehavior();
        bannerDataUserBehavior.setDate(date);
        bannerDataUserBehavior.setShowValue(Double.valueOf(total));
        bannerDataUserBehavior.setAppName(AppNameUtil.getAppFullNameForId(value.getId().intValue()));
        bannerDataUserBehavior.setAppId(String.valueOf(value.getId()));
        bannerDataUserBehavior.setClientId(value.getClientId());
        bannerDataUserBehavior.setAdSpace(adSpace);
        bannerDataUserBehavior.setAppType(value.getAppType());
        bannerDataUserBehavior.setCreatedAt(cdate);
        bannerDataUserBehavior.setActive(active);
        return bannerDataUserBehavior;
    }

    /**
     * 构建mongo的switch语句
     * @param condition
     * @return
     */
    private ConditionalOperators.Switch buildSwitch(AggregationExpression condition) {
        ConditionalOperators.Switch.CaseOperator caseOperator = ConditionalOperators.Switch.CaseOperator
                .when(condition)
                .then(1);
        return ConditionalOperators.Switch.switchCases(caseOperator).defaultTo(0);
    }

    /**
     * 执行mongo的aggregate查询
     * @param agg
     * @param date
     * @return
     */
    private List<UserBehaviorVo> doAgg(Aggregation agg, String date) {
        AggregationResults<UserBehaviorVo> r = secMongoTemplate.aggregate(agg, "xy_burying_point".concat(date), UserBehaviorVo.class);
        return r.getMappedResults();
    }
}
