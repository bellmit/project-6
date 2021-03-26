package com.miguan.ballvideo.service.dsp.impl;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.IPUtil;
import com.miguan.ballvideo.common.util.NumCalculationUtil;
import com.miguan.ballvideo.common.util.adv.AdvFieldType;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.dsp.DspConstant;
import com.miguan.ballvideo.common.util.dsp.DspGlobal;
import com.miguan.ballvideo.dynamicquery.Dynamic2Query;
import com.miguan.ballvideo.dynamicquery.Dynamic3Query;
import com.miguan.ballvideo.entity.dsp.*;
import com.miguan.ballvideo.mapper3.BudgetSmoothMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisRecService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.service.dsp.AdvertDspService;
import com.miguan.ballvideo.service.dsp.nadmin.BudgetSmoothService;
import com.miguan.ballvideo.vo.AbTestAdvParamVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestHeader;
import tool.util.StringUtil;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.miguan.ballvideo.common.util.NumCalculationUtil.roundHalfUpDouble;

/**
 * DSP广告投放Service
 * @author suhongju
 * @date 2020-08-21
 */
@Slf4j
@Service
public class AdvertDspServiceImpl implements AdvertDspService {

    @Resource
    private RedisService redisService;
    @Resource
    private RedisRecService redisRecService;
    @Resource
    private ToolMofangService toolMofangService;
    @Resource
    private Dynamic2Query dynamic2Query;
    @Resource
    private Dynamic3Query dynamic3Query;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private BudgetSmoothService budgetSmoothService;
    @Resource
    private BudgetSmoothMapper budgetSmoothMapper;

/*    @Resource
    DspMapper dspMapper;*/

    /**
     * 查询条件
     * @param queueVo
     * @param param
     * @return
     */
    @Override
    public List<AdvertCodeVo> commonSearch(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        List<AdvertCodeVo> advertCodeVos = getAdvertsByParams(queueVo, param, AdvFieldType.All);
        if(CollectionUtils.isEmpty(advertCodeVos))return null;
        if (advertCodeVos.get(0).getComputer() == 1) {
            //手动配比
            return AdvUtils.computerAndSort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 2){
            //手动排序
            return AdvUtils.sort(advertCodeVos);
        }else if(advertCodeVos.get(0).getComputer() == 3){
            //自动排序
            return AdvUtils.sort(advertCodeVos);
        } else {
            return AdvUtils.sort(advertCodeVos);
        }
    }

    /**
     * 查询一个或多个广告位的广告信息
     * @param param
     * @param fieldType
     * @return
     */
    public List<AdvertCodeVo> getAdvertInfoByParams(AbTestAdvParamsVo queueVo, Map<String, Object> param,int fieldType) {
        String key = AdvUtils.filter(param);
        String json = redisService.get(key);
        if(RedisKeyConstant.EMPTY_VALUE.equals(json)){
            //return null;
        }
        getData(queueVo, param);
        List<AdvertCodeVo> sysVersionVos = dynamic2Query.getAdversWithCache(param,fieldType);
        if(CollectionUtils.isEmpty(sysVersionVos)){
            redisService.set(key,RedisKeyConstant.EMPTY_VALUE,RedisKeyConstant.EMPTY_VALUE_SECONDS);
            return null;
        }else{
            return sysVersionVos;
        }
    }

    //AB实验平台实验Id
    public void getData(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        //queueVo.setAbTestId("[{\"exp_key\":\"ad_exp_446_1\",\"group_id\":143},{\"exp_key\":\"ad_exp_294_0\",\"group_id\":175},{\"exp_key\":\"ad_exp_295_0\",\"group_id\":164},{\"exp_key\":\"ad_exp_103\",\"group_id\":146},{\"exp_key\":\"ad_exp_104\",\"group_id\":178},{\"exp_key\":\"ad_exp_133_0\",\"group_id\":167},{\"exp_key\":\"ad_exp_134_0\",\"group_id\":151},{\"exp_key\":\"ad_exp\",\"group_id\":115},{\"exp_key\":\"ad_exp_340_0\",\"group_id\":171},{\"exp_key\":\"ad_exp_136_0\",\"group_id\":147},{\"exp_key\":\"ad_exp_413_0\",\"group_id\":169},{\"exp_key\":\"ad_exp_412_0\",\"group_id\":156},{\"exp_key\":\"ad_exp_107_0\",\"group_id\":159},{\"exp_key\":\"ad_exp_293_0\",\"group_id\":161},{\"exp_key\":\"ad_exp_137_0\",\"group_id\":154},{\"exp_key\":\"ad_exp_detail\",\"group_id\":121},{\"exp_key\":\"ad_exp_138_0\",\"group_id\":173},{\"exp_key\":\"ad_exp_139_0\",\"group_id\":149}]");
        if (queueVo != null && org.apache.commons.lang3.StringUtils.isNotEmpty(queueVo.getAbTestId())) {
            List<AbTestAdvParamVo> advParamVos = JSON.parseArray(queueVo.getAbTestId(), AbTestAdvParamVo.class);
            Map<String, Object> paramPosition = new HashMap<>();
            paramPosition.put("positionType", param.get("poskey"));
            paramPosition.put("mobileType", param.get("mobileType"));
            paramPosition.put("appPackage", param.get("appPackage"));
            paramPosition.put("queryType", "position");
            List<AdvertCodeVo> advert = dynamic2Query.getAdversWithCache(paramPosition,AdvFieldType.PositionType);
            if (CollectionUtils.isNotEmpty(advert)) {
                Long positionId = advert.get(0).getPositionId();
                if (positionId > 0) {
                    String keyStr = "ad_exp_" + positionId + "_";
                    for (AbTestAdvParamVo advParamVo : advParamVos) {
                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(advParamVo.getExp_key()) && advParamVo.getExp_key().contains(keyStr)) {
                            param.put("queryType", Constant.flow);
                            param.put("abTestId", advParamVo.getGroup_id());
                            break;
                        }
                    }
                }
            }
        }
    }
    /**
     * 查询广告信息
     * @param param
     * @param fieldType
     * @return
     */
    public List<AdvertCodeVo> getAdvertsByParams(AbTestAdvParamsVo queueVo, Map<String, Object> param, int fieldType) {
        //魔方后台-广告总开关:true禁用，false非禁用
        if (toolMofangService.stoppedByMofang(param)) {
            return null;
        }
        return getAdvertInfoByParams(queueVo, param,fieldType);
    }

    @Override
    public Map<String, Integer> judgeValidity(Map<String, Object> param) {
        StringBuffer sql = new StringBuffer("select ");
        sql.append("(SELECT count(1) FROM `ad_app` where package_name = ?) appCnt , ");
        sql.append("(SELECT count(1) FROM `ad_app` where secret_key = ?) secKeyCnt , ");
        sql.append("(SELECT count(1) FROM `ad_advert_position` where position_type = ?) posKeyCnt ");
        sql.append("from dual");
        List<Map<String,Integer>> lst = dynamic2Query.nativeQueryListMap(sql.toString(),param.get("appPackage"),param.get("secretkey"),param.get("poskey"));
        return lst.get(0);
    }


    /**
     * 过滤投放平台没有的广告位，并通过竞价排序
     * @param advertCodeVoList
     * @return
     */
    @Override
    public void cpmAdvCode(List<AdvertCodeVo> advertCodeVoList) {

        Map<String,List<AdvertCodeVo>> advLstMap = advertCodeVoList.stream().collect(Collectors.groupingBy(advertCodeVo -> advertCodeVo.getAdId()));

        //调用全部缓存的Ecpm数据
        Map<String,Double> avgCodeEcpmMap = DspGlobal.getAvgCodeEcpmMap();

        //获取下发广告的ecpm
        List<Map<String,Object>> codeEcpmMapLst = Lists.newArrayList();
        advertCodeVoList.stream().forEach(advertCodeVo -> {
            Map<String,Object> codeEcpmMap = Maps.newHashMap();
            double avg = 0;
            codeEcpmMap.put("code",advertCodeVo.getAdId());
            if(!avgCodeEcpmMap.isEmpty() && avgCodeEcpmMap.get(advertCodeVo.getAdId()) != null){
                avg = avgCodeEcpmMap.get(advertCodeVo.getAdId());
            }
            codeEcpmMap.put("avg", avg);
            codeEcpmMapLst.add(codeEcpmMap);

        });

        //获取排序后的ecpm均值
        Collections.sort(codeEcpmMapLst, new MapComparatorDesc());
        List<String> cpmCodeLst = codeEcpmMapLst.stream().map(codeEcpmMap -> (String)codeEcpmMap.get("code")).collect(Collectors.toList());

        StringBuffer sqlPutStr = new StringBuffer("SELECT DISTINCT cd.code_id,plan.put_in_type from idea_advert_plan plan ");
        sqlPutStr.append(" LEFT JOIN idea_advert_code cd on cd.id = plan.code_id ");
        sqlPutStr.append(" where plan.state = 1 and cd.state = 1 ");
        sqlPutStr.append(" and cd.code_id in ('").append(String.join("','",cpmCodeLst)).append("')");
        List<Map<String,String>> advList =  dynamic3Query.nativeQueryListMap(sqlPutStr.toString());
        List<String> newCodeLst = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(advList)){
            List<String> testJoinBidCode = Lists.newArrayList();
            advList.stream().forEach(adv -> {
                //正式投放：1，测试投放：2
                if(DspConstant.TEST_ISSUE.equals(String.valueOf(adv.get("put_in_type")))){
                    testJoinBidCode.add(String.valueOf(adv.get("code_id")));
                }
            });
            cpmCodeLst.removeAll(testJoinBidCode);
            newCodeLst.addAll(testJoinBidCode);//测试投放放前面
        }
        newCodeLst.addAll(cpmCodeLst);//正式投放+第三方放后面

        advertCodeVoList.clear();
        newCodeLst.stream().forEach(code -> advertCodeVoList.addAll(advLstMap.get(code)));

    }

    static class MapComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Double v1 = Double.parseDouble(m1.get("avg").toString());
            Double v2 = Double.parseDouble(m2.get("avg").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }

    }

    /**
     * 判断是否为98自投平台的代码位
     * @param code
     * @param appPackage
     * @param mobileType
     * @return
     */
    public String judgeAdvPlat(String code, String appPackage, String mobileType){
        //查询dsp平台中适用应用和广告配置平台一致的数据
        StringBuffer sqlStr = new StringBuffer("SELECT app.id from idea_advert_code cd ");
        sqlStr.append(" LEFT JOIN idea_advert_app app on cd.app_id = app.id ");
        sqlStr.append(" where 1 = 1");
        sqlStr.append(" and cd.code_id = ?");
        sqlStr.append(" and app.app_package = ?").append(" and app.mobile_type = ?");
        sqlStr.append(" LIMIT 1");

        List<Object> codeDspLst = dynamic3Query.nativeQueryListMap(sqlStr.toString(),code,appPackage,mobileType);
        if(CollectionUtils.isEmpty(codeDspLst)){
            return "";
        }
        return Optional.ofNullable(""+((Map)codeDspLst.get(0)).get("id")).orElse("");

    }

    @Override
    public AdvertDspInfo getAdvInfo(String appId, String code, String area, String devTp, String device, String uuid, String appVersion) {
        //获取广告内容，过滤应用id与代码位id是否匹配、手机品牌、素材规格、应用版本号
        log.info("获取广告内容,code:{}", code);
        List<AdvDspSqlInfo> advertList1 = getAdvInfoByCache(appId,code,appVersion);


        if(CollectionUtils.isEmpty(advertList1)) return null;

        //过滤地域、投放计划时间段、计划的时间配置、用户兴趣标签
        log.info("过滤地域、投放计划时间段、计划的时间配置,code:{}", code);
        List<AdvDspSqlInfo> advertList2 = advertList1.stream().filter(advertPutIn ->
                filterAdvInfoLstByCon(advertPutIn,area,devTp,uuid)).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(advertList2)) return null;


        //频控策略
        log.info("频控策略,code:{}", code);
        List<AdvDspSqlInfo> advertList3 = filterFrequency(advertList2, device);

        if(CollectionUtils.isEmpty(advertList3)) return null;


        //预算平滑
        log.info("预算平滑,code:{}", code);
        List<AdvDspSqlInfo> advertList4 = filterBudgetSmooth(advertList3);

        if(CollectionUtils.isEmpty(advertList4)) return null;

        //根据计划参竞赛率过滤列表
        log.info("根据计划参竞赛率过滤列表,code:{}", code);
        List<AdvDspSqlInfo> advertList5 = this.filterPartRate(advertList4);

        //创意素材下发，返回广告内容(ecpm竞价)
//        AdvDspSqlInfo info = getDesignIssue(advertList4);
        log.info("创意素材下发，返回广告内容,code:{}", code);
        AdvDspSqlInfo info = filterPlanEcpm(advertList5);
        if(info == null) return null;

        log.info("返回广告：{}", info);
        //保存订单记录
        AdvertDspInfo retDsp = saveIssueOrderRecord(info,device);
        return retDsp;
    }

    /**
     * 改用redis缓存
     * @param appId
     * @param code
     * @param appVersion
     * @return
     */
    private List<AdvDspSqlInfo> getAdvInfoByCache(String appId, String code, String appVersion) {
        String advertList = redisService.get(RedisKeyConstant.GET_ADV_CACHE + "?appId=" +appId + "&code=" + code + "&appVersion=" + appVersion);
        List<AdvDspSqlInfo> advertList1 = Lists.newArrayList();
        if(StringUtils.isEmpty(advertList)){
            advertList1 = dynamic3Query.getAdvInfoLstBySqlCache(appId,code,appVersion);
            if(CollectionUtils.isNotEmpty(advertList1)){
                redisService.set(RedisKeyConstant.GET_ADV_CACHE + "?appId=" +appId + "&code=" + code + "&appVersion=" + appVersion,JSONObject.toJSONString(advertList1),RedisKeyConstant.GET_ADV_CACHE_SECONDS);
            }
        } else {
            advertList1 = JSONObject.parseArray(advertList,AdvDspSqlInfo.class);
        }
        return advertList1;
    }

    /**
     * 上报订单扣费事件
     * @param planId
     */
    @Override
    @Transactional(value = "transactionManager3")
    public synchronized Boolean upOrderReport(String planId) {

        //获取日预算和总预算
        List<Map<String,Object>> planLst = budgetSmoothMapper.findPlanAccountList(Arrays.asList(planId));
        //计划停止
        if(CollectionUtils.isEmpty(planLst)){
            return false;
        }
        Map<String,Object> plan = planLst.get(0);

        double remainDayPrice = ((BigDecimal)plan.get("day_price")).doubleValue();
        double remainTotalPrice = ((BigDecimal)plan.get("total_price")).doubleValue();
        double remainGroupPrice = ((BigDecimal)plan.get("group_day_price")).doubleValue();
        boolean dayPriceFlag = remainDayPrice > 0 ? true : false, totalPriceFlag = remainTotalPrice > 0 ? true : false;
        boolean dayGroupPriceFlag = remainGroupPrice == -1 ? false : true; //计划组日预算-1，表示不限制
        int putInType = (Integer)plan.get("put_in_type"); //投放类型：1：标准投放(预算平滑),2：快速投放

        //如果日预算和总预算都不控制,无限制下发
        if(!dayPriceFlag && !totalPriceFlag){
            return true;
        }

        if(plan.get("acc_id") != null){
            remainDayPrice = ((BigDecimal)plan.get("remain_day_price")).doubleValue();
            remainTotalPrice = ((BigDecimal)plan.get("remain_total_price")).doubleValue();
            remainGroupPrice = ((BigDecimal)plan.get("remain_day_group_price")).doubleValue();
        }else{
            //插入账号剩余预算
            //dspMapper.insertAccount(planId,remainDayPrice,remainTotalPrice);
            this.createdPlanAccount(Integer.parseInt(planId), remainDayPrice, remainGroupPrice, remainTotalPrice);
        }

        //获取点击单价
        double single = ((BigDecimal)plan.get("price")).doubleValue();
        //控制剩余日/总预算，且剩余日/总预算小于预估单价,则不下发
        if((dayPriceFlag && remainDayPrice < single) || (totalPriceFlag && remainTotalPrice < single) || (dayGroupPriceFlag && remainGroupPrice < single)){
            return false;
        }

        //更新账户的剩余日预算和剩余总预算、剩余组预算
        if(dayPriceFlag){
            remainDayPrice -= single;
        }
        if(totalPriceFlag){
            remainTotalPrice -= single;
        }
        if(dayGroupPriceFlag) {
            remainGroupPrice -= single;
        }

        //dspMapper.updateAccount(remainDayPrice,remainTotalPrice,planId);
        String updateSql2 = "update idea_advert_account set remain_day_price = ?, remain_total_price = ?, updated_at = NOW() where plan_id = ?";
        dynamic3Query.nativeExecuteUpdate(updateSql2, new Object[]{remainDayPrice, remainTotalPrice, planId});
        //修改计划组剩余预算
        budgetSmoothService.updateGroupRemainAccount(remainGroupPrice, Integer.parseInt(planId));
        //广告点击后，减少对应时间段的预算值
        budgetSmoothService.reduceTimeSlotPrice(Integer.parseInt(planId), single);

        //如果触发预算平滑，更新平滑时间
        int smoothThresholdValue = DspGlobal.getInt(DspConstant.SMOOTH_THRESHOLD_VALUE);
        if(plan.get("smooth_date") == null){
            if(smoothThresholdValue  >= remainDayPrice){
                //dspMapper.updatePlanSmoothDate(planId);
                String updateSql1 = "update idea_advert_plan set smooth_date = NOW() where id = ?";
                dynamic3Query.nativeExecuteUpdate(updateSql1, new Object[]{planId});
            }
        }

        //如果剩余预算小于单价，则计划停止。不再下发广告
        if(totalPriceFlag && remainTotalPrice < single){
            //dspMapper.updatePlanState(0,planId);
            String updateSql3 = "update idea_advert_plan set state = 0, updated_at = NOW() where id = ?";
            log.info("如果剩余预算小于单价，则计划停止。不再下发广告，planId：{},remainDayPrice：{},remainTotalPrice：{}", planId, remainDayPrice, remainTotalPrice);
            dynamic3Query.nativeExecuteUpdate(updateSql3, new Object[]{planId});
        }
        return true;
    }

    /**
     * 创建计划账户
     * @param planId 计划id
     * @param remainDayPrice 剩余日预算
     * @param remainGroupPrice 剩余组预算
     * @param remainTotalPrice 剩余总预算
     */
    public void createdPlanAccount(Integer planId, double remainDayPrice, double remainGroupPrice, double remainTotalPrice) {
        String insertSql1 = "insert into idea_advert_account(plan_id, remain_day_price,remain_day_group_price, remain_total_price, created_at) VALUES (?,?,?,?,NOW())";
        dynamic3Query.nativeExecuteUpdate(insertSql1, new Object[]{planId, remainDayPrice,remainGroupPrice,remainTotalPrice});
    }

    /**
     * 保存订单并上报埋点
     * @param advZoneValExpVo
     * @param publicInfo
     * @param request
     * @param orderId
     * @param device
     * @param appId
     * @param advertOrderEffectMongodb
     */
    public void saveOrderAndSendToMQ(AdvZoneValExpVo advZoneValExpVo, String publicInfo, HttpServletRequest request,
                                     String orderId, String device, String appId, String advertOrderEffectMongodb) {

        //保存有效点击订单记录
        saveOrderRecord(orderId, device, advZoneValExpVo.getDesign_id(), advZoneValExpVo.getPlan_id(),
                Double.parseDouble(advZoneValExpVo.getPrice()), appId, advertOrderEffectMongodb);

        String abExp = request.getHeader("ab-exp");
        String abIsol = request.getHeader("ab-isol");
        String commonAttr = request.getHeader("Common-Attr");

        //上报埋点  广告有效点击-ad_zone_valid_click
        sendToMQ(advZoneValExpVo, publicInfo, commonAttr, abExp, abIsol, IPUtil.getIpAddr(request));
    }

    /**
     * 判断订单记录是否存在
     * @param appId
     * @param orderId
     * @param advertOrderEffectMongodb
     * @return
     */
    public Boolean judgeOrderExsist(String appId, String orderId, String advertOrderEffectMongodb) {
        //String selectSql1 = "select count(1) ordCnt FROM idea_advert_order_effect where order_id = ?";
        //List<Map<String,Integer>> cntLst = dynamic3Query.nativeQueryListMap(selectSql1, orderId);
        //if(MapUtils.getInteger(cntLst.get(0),"ordCnt") > 0){
        //    return;
        //}
        String collectionName = advertOrderEffectMongodb + "_" + appId;
        Query query = new Query(Criteria.where("order_id").is(orderId));
        return mongoTemplate.exists(query, collectionName);
    }

    /**
     * 保存订单记录
     * @param orderId
     * @param device
     * @param designId
     * @param planId
     * @param price
     * @param appId
     * @param advertOrderEffectMongodb
     */
    private void saveOrderRecord(String orderId, String device, String designId, String planId, Double price,String appId, String advertOrderEffectMongodb) {
        /*
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        String insertSql1 = "insert into idea_advert_order_exposure (id, user_id, plan_id, design_id, order_id, created_at) VALUES (?,?,?,?,?,NOW())";
        dynamic3Query.nativeExecuteUpdate(insertSql1, new Object[]{id, userId, planId, designId, orderId});
        */
        String collectionName = advertOrderEffectMongodb + "_" + appId;
        if (!mongoTemplate.collectionExists(collectionName)) {
            List<IndexModel> indexModels = new ArrayList<>();
            BasicDBObject index1 = new BasicDBObject();
            index1.put("device", 1);
            index1.put("plan_id", 1);
            index1.put("design_id", 1);
            indexModels.add(new IndexModel(index1));
            BasicDBObject index2 = new BasicDBObject();
            index2.put("plan_id", 1);
            indexModels.add(new IndexModel(index2));
            BasicDBObject index3 = new BasicDBObject();
            index3.put("order_id", 1);
            indexModels.add(new IndexModel(index3));
            mongoTemplate.createCollection(collectionName).createIndexes(indexModels);
        }

        //String id = UUID.randomUUID().toString().replaceAll("-", "");
        //IdeaAdvertOrderVo mongodbVo = new IdeaAdvertOrderVo(id, device, planId, designId, orderId, price, new Date(), null);
        Map<String, Object> datas = Maps.newHashMap();//getStringObjectMap(mongodbVo);
        datas.put("device",device);
        datas.put("plan_id",planId);
        datas.put("design_id",designId);
        datas.put("order_id",orderId);
        datas.put("price",price);
        datas.put("created_at",getMongoDate(new Date()));
        datas.put("updated_at",null);
        mongoTemplate.insert(datas, collectionName);
    }


    /**
     * 通过filter逻辑过滤广告内容
     * @param advertPutIn
     * @param area
     * @return
     */
    public boolean filterAdvInfoLstByCon(AdvDspSqlInfo advertPutIn, String area, String devTp, String uuid){
        //过滤地域,类型,1:不限区域，2：指定区域
        if(StringUtils.isNotEmpty(area)){
            area = "市".equals(area.substring(area.length()-1)) ? area.substring(0,area.length()-1) : area;
            if(DspConstant.AREA_TYPE_POINT.equals(advertPutIn.getAreaType()) && StringUtils.isNotEmpty(advertPutIn.getAreaName())
                    && advertPutIn.getAreaName().indexOf(area) == -1){
                return false;
            }
        }

        //过滤手机型号
        if(StringUtils.isNotEmpty(devTp)){
            if(DspConstant.PHONE_TYPE_POINT.equals(advertPutIn.getDevType()) && StringUtils.isNotEmpty(advertPutIn.getDevName())
                    && advertPutIn.getDevName().indexOf(devTp) == -1){
                return false;
            }
        }

        //过滤投放计划时间段
        Date now = new Date();
        if(advertPutIn.getPlanStartDate().after(now) ||
                advertPutIn.getPlanEndDate() != null && advertPutIn.getPlanEndDate().before(now)){//如果结束时间为空，则默认最大不过滤
            return false;
        }

        //过滤计划的时间配置
        //0-全天
        if(DspConstant.TIME_SETTING_ALL_TIME.equals(advertPutIn.getTimeSetting())){
            return true;
        }

        //1-指定开始时间和结束时间
        if(DspConstant.TIME_SETTING_POINT_TIME.equals(advertPutIn.getTimeSetting())){
            if(StringUtils.isNotEmpty(advertPutIn.getTimesConfig())){
                String timeDur = advertPutIn.getTimesConfig();
                String startHr = timeDur.split("-")[0];
                String endHr = timeDur.split("-")[1];
                return isTimeRange(startHr,endHr);
            }
            return false;
        }

        //2-指定多个时段
        if(DspConstant.TIME_SETTING_MANY_TIME.equals(advertPutIn.getTimeSetting())){
            if(StringUtils.isNotEmpty(advertPutIn.getTimesConfig())){
                List<Map<String,Object>> timeConfLst = JSONObject.parseObject(advertPutIn.getTimesConfig(), List.class);
                boolean tcFlag = false;
                for (int i = 0; i < timeConfLst.size(); i++) {
                    if(tcFlag){
                        break;
                    }
                    JSONObject jsonObject = (JSONObject) timeConfLst.get(i);
                    Integer wkDay = (Integer) jsonObject.get("week_day");
                    JSONArray timeDurs = (JSONArray)jsonObject.get("time");
                    for (int j = 0; j < timeDurs.size(); j++) {
                        String weekDay = dayForWeek(now);
                        String timeDur = (String) timeDurs.get(j);
                        String startHr = timeDur.split("-")[0];
                        String endHr = timeDur.split("-")[1];
                        //天数一致，时间点在范围内
                        if (StringUtils.isNotEmpty(weekDay) && Integer.parseInt(weekDay) == wkDay
                                && isTimeRange(startHr,endHr)){
                            tcFlag = true;
                            break;
                        }
                    }
                }
                //如果不包含在时间配置里面，则过滤
                if(!tcFlag){
                    return false;
                }
            }
        }

        //过滤用户兴趣标签
        if(StringUtils.isNotEmpty(uuid) && advertPutIn.getCatType() != null
                && DspConstant.CAT_TYPE_POINT.equals(advertPutIn.getCatType())){
            String userCatIds = redisRecService.hget("bg_sUp", uuid);
            if(!StringUtil.isBlank(advertPutIn.getCatIds()) && !StringUtil.isBlank(userCatIds)) {
                List<String> userCatIdList = Arrays.asList(userCatIds.split(","));
                List<String> planCatIdList = Arrays.asList(advertPutIn.getCatIds().split(","));
                int tag = 0; //0--不包含用户的兴趣标签
                for(String planCatId : planCatIdList) {
                    if(userCatIdList.contains(planCatId)) {
                        tag = 1;
                        break;
                    }
                }
                if(tag == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 频控策略
     * @param advDspSqlInfos
     * @param device
     * @return
     */
    private List<AdvDspSqlInfo> filterFrequency(List<AdvDspSqlInfo> advDspSqlInfos, String device){


/*        StringBuffer sqlPutStr = new StringBuffer("SELECT * from idea_advert_order_exposure where ");
        advDspSqlInfos.stream().forEach(info -> {
            sqlPutStr.append("( plan_id = '").append(info.getPlanId()).append("'");
            sqlPutStr.append("  and design_id = '").append(info.getDesignId()).append("'");
            sqlPutStr.append("  and user_id = '").append(device).append("') or");
        });
        String sql = sqlPutStr.substring(0,sqlPutStr.length()-2);
        List<IdeaAdvertOrderVo> orderList =  dynamic3Query.nativeQueryList(IdeaAdvertOrderVo.class, sql);
        if(CollectionUtils.isEmpty(orderList)){
            return null;
        }
        */
        List<AdvDspSqlInfo> frequencyLst = Lists.newArrayList();
        advDspSqlInfos.stream().forEach(info -> {
            long cnt = 0;
            int frequencyTime = DspGlobal.getInt(DspConstant.FREQUENCY_TIME) * 1000;
            int frequencyCount = DspGlobal.getInt(DspConstant.FREQUENCY_COUNT);

            String collectionName = Constant.ADVERT_ORDER_EXPOSURE_MONGODB + "_" + info.getAppId();
            Query query = new Query(Criteria.where("plan_id").is(info.getPlanId())
                    .and("design_id").is(info.getDesignId())
                    .and("device").is(device));
            query.with(Sort.by(Sort.Order.desc("created_at")));
            query.limit(frequencyCount);

            List<IdeaAdvertOrderVo> ordLst = mongoTemplate.find(query,IdeaAdvertOrderVo.class, collectionName);
            boolean frequencyFlag = false;
            if(CollectionUtils.isNotEmpty(ordLst)){
                for (int i = 0; i < ordLst.size(); i++) {
                    IdeaAdvertOrderVo order = ordLst.get(i);
                    long nowMillis = System.currentTimeMillis();
                    Date createTime = getMinusMongoDate(order.getCreated_at());
                    //创建时间 < 当前时间-频控时间  即频控不触发
                    if (createTime != null && createTime.getTime() < nowMillis - frequencyTime) {
                        frequencyFlag = true;
                        break;
                    }
                }
            }

            if(CollectionUtils.isEmpty(ordLst) || frequencyFlag){
                frequencyLst.add(info);
            }
        });
        return frequencyLst;
    }

    /**
     * 预算平滑
     * @param advDspSqlInfos
     * @return
     */
    private List<AdvDspSqlInfo> filterBudgetSmooth(List<AdvDspSqlInfo> advDspSqlInfos){
        //新的一天的话，重新统计日预算
        this.updateNewDayAccount(advDspSqlInfos);

        //获取计划和账户的关联关系
        List<String> planIds = advDspSqlInfos.stream().map(AdvDspSqlInfo::getPlanId).collect(Collectors.toList());
        List<Map<String,Object>> planLst = budgetSmoothMapper.findPlanAccountList(planIds);

        //获取总剩余可下发次数
        return advDspSqlInfos.stream().filter(info ->{

            //获取计划的广告单价
            double single = info.getPrice();
            String planId = info.getPlanId();
            //1 剩余总预算、剩余日预算已经达不到单价
            double dayPrice = info.getDayPrice(), totalPrice = info.getTotalPrice();
            boolean dayPriceFlag = dayPrice > 0 ? true : false, totalPriceFlag = totalPrice > 0 ? true : false;

            //不存在对应有效计划
            Optional<Map<String,Object>> planMapOpt = planLst.stream().filter(plan -> info.getPlanId().equals(""+plan.get("id"))).findFirst();
            if(!planMapOpt.isPresent()){
                return false;
            }
            Map<String,Object> planMap = planMapOpt.get();
            double dayGroupPrice = ((BigDecimal)planMap.get("group_day_price")).doubleValue();
            boolean dayGroupPriceFlag = dayGroupPrice == -1 ? false : true; //计划组日预算-1，表示不限制
            int putInType = (Integer)planMap.get("put_in_type"); //投放类型：1：标准投放(预算平滑),2：快速投放

            //扣费过
            if(planMap.get("acc_id") != null){
                dayPrice = ((BigDecimal)planMap.get("remain_day_price")).doubleValue();
                totalPrice = ((BigDecimal)planMap.get("remain_total_price")).doubleValue();
                dayGroupPrice = ((BigDecimal)planMap.get("remain_day_group_price")).doubleValue();
            }

            // 1）如果日预算和总预算都不控制,无限制下发
            if(!dayPriceFlag && !totalPriceFlag){
                return true;
            }
            // 2） 仅控制总预算、控制总预算与日预算 （当前单价大于预算）
            //控制剩余日/总预算，且剩余日/总预算小于预估单价,则不下发
            if((dayPriceFlag && dayPrice < single) || (totalPriceFlag && totalPrice < single) || (dayGroupPriceFlag && dayGroupPrice < single)){
                return false;
            }

            //2 触发预算平滑控量机制
            //触发平滑预算控量时间为空 || 总预算不控制，则不控制预算平滑
            if(planMap.get("smooth_date") == null){
                //如果是标准投放，则当前时间段的预算，是否还有剩余
                if(putInType == 1 && !budgetSmoothService.isHasBudget(Integer.parseInt(planId), single)) {
                    return false;
                }
                return true;
            }
            Date smoothDate = (Date)planMap.get("smooth_date");


            int smoothCount = DspGlobal.getInt(DspConstant.SMOOTH_COUNT);
            int smoothDuration = DspGlobal.getInt(DspConstant.SMOOTH_DURATION);
            int smoothThresholdValue = DspGlobal.getInt(DspConstant.SMOOTH_THRESHOLD_VALUE);

            Date now = new Date();
            //如果当前时间-预算平滑启动时间>预算平滑总时长，则已经预算平滑完成，不下发广告
            if(now.getTime() - smoothDate.getTime() > smoothCount*smoothDuration*60*1000){
                return false;
            }

            String collectionName = Constant.ADVERT_ORDER_EFFECT_MONGODB + "_" + info.getAppId();
            Query query = new Query(Criteria.where("plan_id").is(info.getPlanId()));
            query.addCriteria(Criteria.where("created_at").gte(smoothDate));
            //query.with(Sort.by(Sort.Order.desc("created_at")));
            //query.limit(1);
            List<IdeaAdvertOrderVo> orderEffectLst = mongoTemplate.find(query,IdeaAdvertOrderVo.class, collectionName);
            //如果找不到对应订单，代表没有扣费过。准许下发
            if(CollectionUtils.isEmpty(orderEffectLst)){
                return true;
            }
            //IdeaAdvertOrderVo orderEffect = orderEffectLst.get(0);
            //获取最新扣费时间
            //Date createdAt = getMinusMongoDate(orderEffect.getCreated_at());

            //计算第N次区间时长的平滑
            double smoothCnt = Math.ceil((now.getTime() - smoothDate.getTime())/
                    (double)(smoothDuration*60*1000));
            if(smoothCnt > 0){
                //当前预算平滑下发的广告次数
                double currSmthCnt = orderEffectLst.size();

                //每个区间的价格
                double smoothSingle = smoothThresholdValue/smoothCount;

                //第N次区间能分发的价格
                double smoothMoney = smoothCnt * smoothSingle;

                //当前预算平滑下的分发价格
                double currSmthMoney = currSmthCnt * single;

                //当前预算平滑总价格 大于 第N次区间能分发的价格
                if(currSmthMoney > smoothMoney){
                    return false;
                }

            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 新的一天的话，重新统计日预算
     * @param list
     */
    public void updateNewDayAccount(List<AdvDspSqlInfo> list) {
        if(list == null || list.isEmpty()) {
            return;
        }
        List<String> planIds = list.stream().map(AdvDspSqlInfo::getPlanId).collect(Collectors.toList());
        budgetSmoothMapper.initAdvertAcount(planIds);
//        budgetSmoothMapper.initPlanSmoothDate(planIds);
    }

    /**
     * 创意素材下发，返回广告内容
     * @param advDspSqlInfos
     * @return
     */
    private AdvDspSqlInfo getDesignIssue(List<AdvDspSqlInfo> advDspSqlInfos){

        List<AdvDspSqlInfo> dspSqlInfos = advDspSqlInfos.stream()
                .filter(info -> DspConstant.TEST_ISSUE.equals(info.getPutInType())).collect(Collectors.toList());

        AdvDspSqlInfo info =  null;
        //存在测试计划,走计划权重，否则走ecpm竞价
        if(CollectionUtils.isNotEmpty(dspSqlInfos)){
            info = filterDesignWeight(dspSqlInfos);
        }else{
            info = filterPlanEcpm(advDspSqlInfos);
        }
        return info;
    }

    /**
     * 保存订单记录
     * @param info
     * @param device
     * @return
     */
    public AdvertDspInfo saveIssueOrderRecord(AdvDspSqlInfo info,String device) {
        AdvertDspInfo advertDspInfo = new AdvertDspInfo();
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        advertDspInfo.setCode(info.getCodeId());
        advertDspInfo.setAppId(info.getAppId());
        advertDspInfo.setPlanId(info.getPlanId());
        advertDspInfo.setDesignId(info.getDesignId());
        advertDspInfo.setUsrId(info.getUsrId());
        //advertDspInfo.setDesignName(info.getDesName());
        advertDspInfo.setMaterialType(info.getMaterialType());
        advertDspInfo.setMaterialUrl(info.getMaterialUrl());
        //(1)创意勾选了【展示产品名称与品牌logo】 (2) 判断产品名称 和 品牌logo是否都非空。满足这2个条件，才下发产品名称和品牌logo
        if(info.getIsShowLogoProduct() == 1 && StringUtil.isNotBlank(info.getLogoUrl()) && StringUtil.isNotBlank(info.getProductName())) {
            advertDspInfo.setUserLogo(info.getLogoUrl());
            advertDspInfo.setProductName(info.getProductName());
        }
        advertDspInfo.setIdeaTitle(info.getCopy());
        advertDspInfo.setButtonIdeaTitle(info.getButtonCopy());
        advertDspInfo.setUserName(info.getProductName());
        advertDspInfo.setPutInMethod(info.getPutInMethod());
        advertDspInfo.setPutInValue(info.getPutInValue());
        advertDspInfo.setAdvSource(DspConstant.ADVERT_PLAT_98);
        advertDspInfo.setView(true);
        advertDspInfo.setOrderId(orderId);
        advertDspInfo.setPositionType(info.getPositionType());
        advertDspInfo.setStyleSize(info.getStyleSize());
        advertDspInfo.setPrice(info.getPrice());
        //插入一条订单记录
/*        String insertSql = "insert into idea_advert_order(id, state, created_at) VALUES (?,'1',NOW())";
        dynamic3Query.nativeExecuteUpdate(insertSql,new Object[]{orderId});*/
        saveOrderRecord(orderId, device, info.getDesignId(), info.getPlanId(), info.getPrice(), info.getAppId(),  Constant.ADVERT_ORDER_MONGODB);
        return advertDspInfo;
    }

    //根据计划参竞赛率过滤列表
    private List<AdvDspSqlInfo> filterPartRate(List<AdvDspSqlInfo> list) {
        List<AdvDspSqlInfo> result = new ArrayList<>();
        String maxPlanId = "";  //最大参竞率的计划id
        double maxPartRate = -1D;  //最大参竞率

        Map<String, List<AdvDspSqlInfo>> map = list.stream().collect(Collectors.groupingBy(AdvDspSqlInfo::getPlanId));
        for(Map.Entry<String, List<AdvDspSqlInfo>> entry : map.entrySet()) {
            String planId = entry.getKey();
            String hitPart = "true-1";
            if("1".equals(entry.getValue().get(0).getPutInType())) {
                //快速投放才需要参竞,普通投放不需要
                budgetSmoothService.isHitPartRate(Integer.parseInt(planId));
            }
            String[] hitPartArry = hitPart.split("-");
            boolean isHit = Boolean.parseBoolean(hitPartArry[0]);  //参竞率命中结果
            double partRate = Double.parseDouble(hitPartArry[1]);  //计划对应的参竞率
            //计算出参竞率最大的计划id
            if(partRate > maxPartRate) {
                maxPartRate = partRate;
                maxPlanId = planId;
            }
            if(isHit) {
                //计划命中参竞率，则把计划放到返回结果中
                result.addAll(entry.getValue());
            }
        }

        if(result.isEmpty()) {
            //如果没有一个计划命中参竞率，则返回参竞率最高的计划
            return map.get(maxPlanId);
        } else {
            return result;
        }
    }

    public static void main(String[] args) {
        int a = 100;
        double b = 0.025;

        System.out.println((int)Math.round(a*b));
    }

    /**
     * 获取计划中ecpm最高的广告内容
     * @param advDspSqlInfos
     * @return
     */
    private AdvDspSqlInfo filterPlanEcpm(List<AdvDspSqlInfo> advDspSqlInfos){
        if(advDspSqlInfos == null) {
            return null;
        }
        //调用ecpm缓存，获取所有98自投的代码位有效曝光数
        Map<String, Integer> all98duShowMap = DspGlobal.getAll98duShowMap();
        //调用ecpm缓存，获取所有98自投的代码位有点击数
        Map<String, Integer> all98duClickMap = DspGlobal.getAll98duClickMap();

        List<Map<String,Object>> planLst = Lists.newArrayList();
        for(AdvDspSqlInfo info : advDspSqlInfos) {
            Map<String,Object> planMap = Maps.newHashMap();
            double price = info.getPrice();
            planMap.put("plan_id", info.getPlanId());
            planMap.put("design_id", info.getDesignId());
            planMap.put("code", info.getCodeId());
            planMap.put("ecpm",(double)0);
            String key = info.getPlanId() + "_" + info.getDesignId() + "_" + info.getCodeId();
            if(!all98duShowMap.isEmpty() && all98duShowMap.get(key) != null && !all98duClickMap.isEmpty() && all98duClickMap.get(key) != null){
                //冷启动ecpm计算
                double coldBootEcpm = coldBootEcpm(all98duShowMap.get(key), all98duClickMap.get(key), price);
                log.info("冷启动ecpm计算结果，key:{}, ecpm:{}",key, coldBootEcpm);
                planMap.put("ecpm",coldBootEcpm);
            }
            planLst.add(planMap);
        }

        //获取计划最大的ecpm
        int maxEcpmSize = 0;
        double maxEcpm = 0;
        List<String> maxEcpmLst = Lists.newArrayList();

        for (int i = 0; i < planLst.size(); i++) {
            Map<String,Object> planMap = planLst.get(i);
            if((Double) planMap.get("ecpm") >= maxEcpm){
                maxEcpm = (Double) planMap.get("ecpm");
            }
        }
        for (int i = 0; i < planLst.size(); i++) {
            Map<String,Object> planMap = planLst.get(i);
            if((Double) planMap.get("ecpm") == maxEcpm){
                maxEcpmLst.add(planMap.get("plan_id") + "_" + planMap.get("design_id") + "_" + planMap.get("code"));
            }
        }
        maxEcpmSize = maxEcpmLst.size();

        //如果ecpm有多个一个大，则取随机数
        int max= maxEcpmSize,min=1;
        int rand = (int) (Math.random()*(max-min)+min);
        List<AdvDspSqlInfo> maxEcpmsAdvInfoLst = advDspSqlInfos.stream()
                .filter(r -> maxEcpmLst.get(rand-1).equals(r.getPlanId() + "_" + r.getDesignId() + "_" + r.getCodeId())).collect(Collectors.toList());
        return maxEcpmsAdvInfoLst.get(0);
    }


    /**
     *
     * 冷启动ecpm计算
     * ECPM=1000*(冷启动点击数a + 当天实际点击数d）/（冷启动曝光数b + 当天实曝光击数c） * 点击单
     * 冷启动点击数a = 98平台的计划昨天的平均点击率 * 冷启动曝光数b（冷启动曝光数b,后台配置）
     *
     * @param showC 实时有效曝光数
     * @param clickD 实时有效点击数
     * @param price 点击单价
     * @return
     */
    private double coldBootEcpm(int showC, int clickD, double price) {
        double preClickRate = budgetSmoothService.staYesPreClickRate();  //98平台的计划昨天的平均点击率
        int showB = DspGlobal.getInt(DspConstant.COLD_BOOT_SHOW);  //冷启动曝光数
        int clickA = (int)Math.round(preClickRate * showB);  //冷启动点击数a = 98平台的计划昨天的平均点击率 * 100（冷启动曝光数）
        double ecpm = 1000 * (clickA + clickD) / (showB + showC) * price;
        return roundHalfUpDouble(ecpm);  //四舍五入，保留小数点后2位
    }

    /**
     * 根据权重高低下发一个素材
     * @param advDspSqlInfos
     * @return
     */
    private AdvDspSqlInfo filterDesignWeight(List<AdvDspSqlInfo> advDspSqlInfos){
        //权重总值
        int sum = 0;
        //每个创意下权重区间
        Map<String,String> weightMap = Maps.newHashMap();
        for (int i = 0; i < advDspSqlInfos.size(); i++) {
            AdvDspSqlInfo info = advDspSqlInfos.get(i);
            int weight = info.getWeight();
            weightMap.put(info.getDesignId(),sum + "," + (sum + weight));
            sum += weight;
        }
        AdvDspSqlInfo retInfo = null;
        //如果权重都为0，则返回空
        if(sum == 0){
            return  retInfo;
        }
        //随机获取权重值
        int max= sum,min=1;
        int rand = (int) (Math.random()*(max-min)+min);
        for (int i = 0; i < advDspSqlInfos.size(); i++) {
            AdvDspSqlInfo info = advDspSqlInfos.get(i);
            //获取权重区间
            String weightBet = weightMap.get(info.getDesignId());
            Integer minWei = Integer.parseInt(weightBet.split(",")[0]);
            Integer maxWei = Integer.parseInt(weightBet.split(",")[1]);
            if(rand > minWei && rand <= maxWei){
                retInfo = info;
                break;
            }
        }
        return retInfo;
    }

    /**
     * IP：10 path:/usr/local/webserver/xyadcoll/data/ad_log,生成MQ
     * @param advZoneValExpVo
     * @param publicInfo
     * @param userIp
     */
    public void sendToMQ(AdvZoneValExpVo advZoneValExpVo, String publicInfo, String commonAttr, String abExp, String abIsol, String userIp) {
        JSONObject event = new JSONObject();
        if(StringUtil.isNotBlank(commonAttr)) {
            byte[] presetPropByte = Base64Utils.decodeFromString(commonAttr);
            commonAttr = new String(presetPropByte, StandardCharsets.UTF_8);
            String[] attr = commonAttr.split("::");
            event.put("change_channel", attr[0]);
            event.put("is_login", attr[1]);
            event.put("uuid", attr[2]);
            event.put("app_version", attr[4]);
            event.put("last_view", attr[5]);
            event.put("view", attr[6]);
        }

        String jsonStr = JSONObject.toJSONString(advZoneValExpVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.keySet().forEach(e -> event.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        JSONObject p = new JSONObject();
        p.put("publicInfo", publicInfo);
        p.put("ab-exp", abExp);
        p.put("ab-isol", abIsol);
        p.put("event", event.toJSONString());
        p.put("userIp", userIp);
        p.put("actionType", "ad_action");
        String rabbitJsonStr = p.toJSONString();
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE, RabbitMQConstant.BURYPOINT_RUTE_KEY, rabbitJsonStr);
        log.info("生产MQ,exchange="+RabbitMQConstant.BURYPOINT_EXCHANGE + ",routeKey="+RabbitMQConstant.BURYPOINT_RUTE_KEY+",rabbitJsonStr="+rabbitJsonStr);
    }



    /**
     * 数据转json格式
     * @param mongodbVo
     * @return
     */
    private Map<String, Object> getStringObjectMap(IdeaAdvertOrderVo mongodbVo) {
        Map<String, Object> datas = new ConcurrentHashMap<>(100);
        String jsonStr = JSONObject.toJSONString(mongodbVo);
        Map<String,Object> jsonMap = JSONObject.parseObject(jsonStr);
        jsonMap.keySet().forEach(e -> datas.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e),jsonMap.get(e)));
        return datas;
    }

    /**
     * 查询星期几
     * @param tmpDate
     * @return
     */
    public String dayForWeek(Date tmpDate){
        try {

            Calendar cal = Calendar.getInstance();

            String[] weekDays = { "7", "1", "2", "3", "4", "5", "6" };

            cal.setTime(tmpDate);

            int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。

            if (w < 0)
                w = 0;

            return weekDays[w];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断是否在时间区间内
     * @param startH
     * @param endH
     * @return
     * @throws ParseException
     */
    private boolean isTimeRange(String startH, String endH) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date now = df.parse(df.format(new Date()));
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTime(now);

            Date begin = df.parse(startH);
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(begin);

            Date end = df.parse(endH);
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(end);


            if (nowTime.before(endTime) && nowTime.after(beginTime)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostConstruct
    @Override
    public void initDspConfig() {
        String selectSql = "select * FROM idea_advert_config where state = 1";
        List<IdeaAdvertConfigVo> conLst = dynamic3Query.nativeQueryList(IdeaAdvertConfigVo.class,selectSql);
        if(CollectionUtils.isEmpty(conLst)){
            return;
        }
        DspGlobal.putConfigAll(conLst);
        //清除redis缓存
        Set<String>  advkeys = redisService.keys(CacheConstant.GET_ADVERSWITHCACHE + "*");
        if(CollectionUtils.isNotEmpty(advkeys)){
            advkeys.forEach(key -> redisService.del(key));
        }
        Set<String>  infokeys = redisService.keys(CacheConstant.GET_ADV_INFO_LST_BY_SQL + "*");
        if(CollectionUtils.isNotEmpty(infokeys)){
            infokeys.forEach(key -> redisService.del(key));
        }
    }

    /**
     * 每5分钟跑一次加载ecpm数据
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    public void scheduledDspEcpm() {
        String selectSql = "select * FROM idea_advert_ecpm";
        List<IdeaAdvertEcpmVo> ecpmLst = dynamic3Query.nativeQueryList(IdeaAdvertEcpmVo.class,selectSql);
        if(CollectionUtils.isEmpty(ecpmLst)){
            return;
        }
        DspGlobal.putEcpmAll(ecpmLst);
    }


    /**
     * 计算得到MongoDB存储的日期，（默认情况下mongo中存储的是标准的时间，中国时间是东八区，存在mongo中少8小时，所以增加8小时）
     */
    public static Date getMongoDate(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, 8);
        return ca.getTime();
    }

    public static Date getMinusMongoDate(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, -8);
        return ca.getTime();
    }
}
