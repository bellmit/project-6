package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.advert.domain.mapper.AdAdvertCodeMapper;
import com.miguan.advert.domain.mapper.AdAdvertConfigCodeMapper;
import com.miguan.advert.domain.mapper.AdPlatMapper;
import com.miguan.advert.domain.mapper.PositionInfoMapper;
import com.miguan.advert.domain.pojo.AdAdvertCode;
import com.miguan.advert.domain.service.AdAdvertCodeService;
import com.miguan.advert.domain.service.ToolMofangService;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdAdvertCodeQuery;
import com.miguan.advert.domain.vo.result.PositionInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AdAdvertCodeServiceImpl implements AdAdvertCodeService {

    @Resource
    private AdAdvertCodeMapper adAdvertCodeMapper;
    @Resource
    private AdPlatMapper adPlatMapper;
    @Resource
    private AdAdvertConfigCodeMapper adAdvertConfigCodeMapper;
    @Resource
    private ToolMofangService toolMofangService;
    @Resource
    private PositionInfoMapper positionInfoMapper;

    @Override
    public PageVo getList(String url, Integer page, Integer page_size, AdAdvertCodeQuery query) {
        PageHelper.startPage(page,page_size);
        Page<AdAdvertCode> pageInfo = adAdvertCodeMapper.findQueryPage(query);
        List<AdAdvertCode> result = pageInfo.getResult();
        for (AdAdvertCode adAdvertCode:result) {
            if(adAdvertCode.getMaterial_name() == null){
                adAdvertCode.setMaterial_name("");
            }
            if(adAdvertCode.getApp_name() == null){
                adAdvertCode.setApp_name("-");
            }
            if(adAdvertCode.getLadder() == null || adAdvertCode.getLadder() != 1){
                adAdvertCode.setLadder_price("-");
            }
            if(adAdvertCode.getPlat_name() == null){
                adAdvertCode.setPlat_name("");
            }
            if(adAdvertCode.getRander_name() == null){
                adAdvertCode.setRander_name("");
            }
            if(adAdvertCode.getType_name() == null){
                adAdvertCode.setType_name("");
            }
            int count = adAdvertConfigCodeMapper.existCode(adAdvertCode.getId());
            if(count>0){
                adAdvertCode.setDel_status(1);
            } else {
                adAdvertCode.setDel_status(2);
            }
            if(adAdvertCode.getChannel_type() != null && adAdvertCode.getChannel_type() == 1){
                adAdvertCode.setChannel_name("全部渠道");
            } else {
                if(StringUtils.isNotEmpty(adAdvertCode.getChannel_ids())){
                    List<ChannelInfoVo> channelInfoVos = toolMofangService.findChannelInfo();
                    getChannelName(channelInfoVos, adAdvertCode);
                }
            }
            String[] positionName = positionInfoMapper.getPositionName(adAdvertCode.getId());
            if(positionName != null && positionName.length>0){
                adAdvertCode.setPosition_name(changStrArr(positionName));
            }

        }
        return new PageVo(pageInfo,url);
    }

    private String changStrArr(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strArr.length ; i++) {
            if(i == 0){
                sb.append(strArr[i]);
            }else {
                sb.append("、"+strArr[i]);
            }
        }
        return sb.toString();
    }


    //获取渠道名称信息
    private void getChannelName(List<ChannelInfoVo> channelInfoVos, AdAdvertCode adAdvertCode) {
        String channelIds = adAdvertCode.getChannel_ids();
        if (StringUtils.isNotEmpty(channelIds)) {
            String[] channelIdStr = channelIds.split(",");
            String channelNames = "";
            for (int i=0;i<channelIdStr.length;i++) {
                for (ChannelInfoVo channelInfoVo : channelInfoVos) {
                    if (channelIdStr[i].equals(channelInfoVo.getChannelId())) {
                        if (i == channelIdStr.length - 1) {
                            channelNames += channelInfoVo.getChannelName();
                        } else {
                            channelNames += channelInfoVo.getChannelName() + "、";
                        }
                        break;
                    }
                }
            }
            adAdvertCode.setChannel_name(channelNames);
        }
    }

    @Override
    public String[] findAdCodeAdId(String adId) {
        return adAdvertCodeMapper.findAdCodeAdId(adId);
    }
}
