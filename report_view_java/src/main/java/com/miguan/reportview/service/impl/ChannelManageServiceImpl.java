package com.miguan.reportview.service.impl;

import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.entity.FatherChannel;
import com.miguan.reportview.mapper.ChannelDetailMapper;
import com.miguan.reportview.mapper.ChannelManageMapper;
import com.miguan.reportview.mapper.DwVideoActionsMapper;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.IChannelDataService;
import com.miguan.reportview.service.IChannelManageService;
import com.miguan.reportview.service.IUserKeepService;
import com.miguan.reportview.vo.ChannelDataVo;
import com.miguan.reportview.vo.LdUserContentDataVo;
import com.miguan.reportview.vo.ParamsBuilder;
import com.miguan.reportview.vo.UserKeepVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 渠道维表管理
 */
@Service
public class ChannelManageServiceImpl implements IChannelManageService {

    @Resource
    private ChannelManageMapper channelManageMapper;



    public int addFatherChannel(FatherChannel channelDO){
        return channelManageMapper.addFatherChannel(channelDO);
    };

    public int updateFatherChannel(FatherChannel channelDO){
        return channelManageMapper.updateFatherChannel(channelDO);
    };

    public int deleteFatherChannel(Long id){
        return channelManageMapper.deleteFatherChannel(id);
    };

    public List<FatherChannel> listFatherChannel(Map<String, Object> map){
        return channelManageMapper.listFatherChannel(map);
    };

    public int getFatherChannelCount(Map<String, Object> map){
        return channelManageMapper.getFatherChannelCount(map);
    };

    public List<String> listFatherChannelYm(){
        return channelManageMapper.listFatherChannelYm();
    };

    public List<String> listOwner(){
        return channelManageMapper.listOwner();
    };

    public FatherChannel getFatherChannel(Long id){
        return channelManageMapper.getFatherChannel(id);
    };

}
