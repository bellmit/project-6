package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.PageInfo;
import com.miguan.xuanyuan.entity.XyApp;
import com.miguan.xuanyuan.mapper.XyAppMapper;
import com.miguan.xuanyuan.service.XyAdPositionService;
import com.miguan.xuanyuan.service.XyAppService;
import com.miguan.xuanyuan.service.XySourceAppService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.XyAppDetailVo;
import com.miguan.xuanyuan.vo.XyAppSimpleVo;
import com.miguan.xuanyuan.vo.XyAppVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 应用
 * @Date 2021/1/21
 **/
@Slf4j
@Service
public class XyAppServiceImpl implements XyAppService {

    @Resource
    private XyAppMapper mapper;


    @Resource
    private RedisService redisService;
    @Resource
    private XyAdPositionService adPositionService;
    @Resource
    XySourceAppService xySourceAppService;

//    @Resource
//    private RestTemplate restTemplate;
//
//
//    @Value("${api.business.buildAppInfo}")
//    private String buildAppInfo;

    @Override
    public void save(XyApp xyApp) throws Exception {
        int count = mapper.judgeExistPackage(xyApp.getPackageName(),xyApp.getId());
        if(count > 0){
            throw new ValidateException("该包名已存在！");
        }
        if(xyApp.getId() == null){
            //创建应用时，去技术中台生成appKey
//            buildBusiAppInfo(xyApp);
            mapper.insert(xyApp);
            //创建内部的三方应用;
            xySourceAppService.createInnerApp(xyApp.getId());
        } else {
            mapper.update(xyApp);
        }
    }

//    private void buildBusiAppInfo(XyApp xyApp) throws ServiceException {
//        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
//        AppInfoReq req = new AppInfoReq();
//        req.setAppPackage(xyApp.getPackageName());
//        req.setPlatform(ClientEnum.getNameByType(xyApp.getClientType()));
//        body.add("addVo",req);
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//        ResultMap result = null;
//        try {
//            ResponseEntity<ResultMap> responseInfo = restTemplate.postForEntity(buildAppInfo,new HttpEntity<AppInfoReq>(req,requestHeaders),ResultMap.class);
//            result = responseInfo.getBody();
//        } catch (Exception e){
//            log.info("调用技术中台，创建应用失败。失败原因{}",e.getMessage());
//            throw new ServiceException("抱歉，系统异常，请联系管理员。");
//        }
//        if(result != null && result.getCode() == 200 && result.getData() != null){
//            //创建成功
//            AppInfo appInfo = JSONObject.parseObject(JSONObject.toJSONString(result.getData()),AppInfo.class);
//            xyApp.setAppKey(appInfo.getAppKey());
//            xyApp.setAppSecret(appInfo.getAppSecret());
//        } else {
//            log.info("调用技术中台,创建应用失败。{}",(result == null ? "" : result.getMessage()));
//            throw new ServiceException("调用技术中台,创建应用失败。" + (result == null ? "" : result.getMessage()));
//        }
//    }

    @Override
    public PageInfo<XyAppVo> pageList(int plat, Long userId, String userName, Integer type, String keyword, Integer clientType, Integer status, Integer pageNum, Integer pageSize) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("plat",plat);
        params.put("userId",userId);
        params.put("userName",userName);
        params.put("type",type);
        params.put("keyword",keyword);
        params.put("clientType",clientType);
        params.put("status",status);
        PageHelper.startPage(pageNum, pageSize);
        Page<XyAppVo> pageResult = mapper.findPageList(params);
        List<XyAppVo> result = pageResult.getResult();
        if(CollectionUtils.isNotEmpty(result)){
            result.forEach(r -> {
                if(r.getClientType() == 1){
                    r.setClientLogo(redisService.get(RedisKeyConstant.CONFIG_CODE + RedisKeyConstant.ANDROID_LOGO));
                } else {
                    r.setClientLogo(redisService.get(RedisKeyConstant.CONFIG_CODE + RedisKeyConstant.IOS_LOGO));
                }
            });
        }
        return new PageInfo(pageResult);
    }

    @Override
    public XyAppDetailVo findById(Long id) {
        return mapper.findDetailById(id);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
        //需要删除广告位管理和聚合管理的所有内容。
        adPositionService.deleteByAppId(id);
    }

    @Override
    public void updateStatus(Long appId, Integer status) {
        mapper.updateStatus(appId,status);
    }

    @Override
    public List<XyAppSimpleVo> findList(int plat, Long userId) {
        return mapper.findList(plat,userId);
    }

    @Override
    public boolean existAppInfo(String appKey, String secretKey, String SHA1) {
        return mapper.existAppInfo(appKey, secretKey,SHA1, XyConstant.APP_STATUS_START) > 0 ? true : false;
    }

    @Override
    public XyApp findByAppKeyAndSecret(String appKey, String secretKey) {
        return mapper.findByAppKeyAndSecret(appKey,secretKey, XyConstant.APP_STATUS_START);
    }

}
