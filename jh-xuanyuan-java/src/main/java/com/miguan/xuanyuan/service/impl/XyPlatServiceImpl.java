package com.miguan.xuanyuan.service.impl;

import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyPlat;
import com.miguan.xuanyuan.mapper.XyPlatMapper;
import com.miguan.xuanyuan.service.XyAdPositionService;
import com.miguan.xuanyuan.service.XyPlatService;
import com.miguan.xuanyuan.service.common.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 广告平台
 * @Date 2021/1/21
 **/
@Service
public class XyPlatServiceImpl implements XyPlatService {

    @Resource
    private XyPlatMapper mapper;

    @Override
    public void save(XyPlat xyPlat) throws ValidateException {
        int count = mapper.judgeExistKey(xyPlat.getPlatKey(),xyPlat.getId());
        if(count > 0){
            throw new ValidateException("该KEW已存在！");
        }
        if(xyPlat.getId() == null){
            mapper.insert(xyPlat);
        } else {
            mapper.update(xyPlat);
        }
    }

    @Override
    public XyPlat findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public List<XyPlat> findList() {
        return mapper.findList();
    }

    public XyPlat getPlatDataByPlatKey(String platKey) {
        return mapper.getPlatDataByPlatKey(platKey);
    }

    @Override
    public List<XyPlat> findByAdType(String adType) {
        return mapper.findByAdType(adType);
    }

}
