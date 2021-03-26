package com.miguan.xuanyuan.service.impl;

import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.entity.XyRender;
import com.miguan.xuanyuan.mapper.XyRenderMapper;
import com.miguan.xuanyuan.service.XyRenderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 渲染方式
 * @Date 2021/1/21
 **/
@Service
public class XyRenderServiceImpl implements XyRenderService {

    @Resource
    private XyRenderMapper mapper;

    @Override
    public void save(XyRender xyRender) throws ValidateException {
        int count = mapper.judgeExistKey(xyRender.getPlatKey(),xyRender.getId());
        if(count > 0){
            throw new ValidateException("该KEW已存在！");
        }
        if(xyRender.getId() == null){
            mapper.insert(xyRender);
        } else {
            mapper.update(xyRender);
        }
    }

    @Override
    public XyRender findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public List<XyRender> findList(String platKey, String adType) {
        return mapper.findList(platKey,adType);
    }

}
