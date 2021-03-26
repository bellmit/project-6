package com.miguan.idmapping.service.clickhourse;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.idmapping.entity.UuidMapperCH;
import com.miguan.idmapping.mapper.UuidMapperMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhongli
 * @date 2020-09-02 
 *
 */
@DS("clickhouse")
@Service
public class ClickhourseService extends ServiceImpl<UuidMapperMapper, UuidMapperCH> implements IService<UuidMapperCH> {

    @Resource
    private UuidMapperMapper uuidMapperMapper;

    public void addUserMapper(UuidMapperCH uuidMapperCH) {
        uuidMapperMapper.insert(uuidMapperCH);
    }
}

