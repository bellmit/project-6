package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.LdInterestTag;
import com.miguan.laidian.entity.LdUserTagRelation;
import com.miguan.laidian.mapper.LdInterestTagMapper;
import com.miguan.laidian.service.LdInterestTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chenweijie
 * @Date: 2020/10/26 11:09
 * @Description:
 */
@Slf4j
@Service
public class LdInterestTagServiceImpl implements LdInterestTagService {
    @Resource
    private LdInterestTagMapper ldInterestTagMapper;

    /**
     * 获取用户标签
     * @param userId
     * @return
     */
    @Override
    public List<LdInterestTag> getUserTags(String userId) {
        //获取全部标签
        List<LdInterestTag> allList = ldInterestTagMapper.getAllTags();
        //获取用户标签
        List<LdUserTagRelation> list =  ldInterestTagMapper.getUserTagRelation(userId);
        List<Integer> userTagIds = new ArrayList<>();
        for (LdUserTagRelation info : list) {
            userTagIds.add(info.getTagId());
        }
        for (LdInterestTag info : allList) {
            if (userTagIds.contains(info.getId())) {
                info.setCheck(true);
            }
        }
        return allList;
    }

    /**
     * 获取全部标签
     * @return
     */
    public List<LdInterestTag> getAllList() {
        //获取全部标签
        return ldInterestTagMapper.getAllTags();
    }


    /**
     * 保存用户标签
     * @param userId
     * @param tagIdsStr
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveTag(String userId, String tagIdsStr, String deviceId) {
        //逗号隔开的字符串，转数组
        String[] tagIdsArr = tagIdsStr.split(",");
        int[] array = Arrays.stream(tagIdsArr).mapToInt(Integer::parseInt).toArray();
        List<Integer> tagIds = Arrays.stream(array).boxed().collect(Collectors.toList());
        //删除标签
        ldInterestTagMapper.deleteDeviceTag(deviceId);

        //添加
        Integer num = 0;
        for (Integer tagId : tagIds) {
            if (null != deviceId && !"".equals(deviceId)) {
                Integer result = ldInterestTagMapper.insertUserTag(userId, tagId, deviceId);
                if (result > 0) {
                    num++;
                }
            }
        }
        return num;
    }
}
