package com.miguan.ballvideo.service.dsp.impl;

import com.cgcg.base.core.exception.CommonException;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.mapper3.AdvertGroupMapper;
import com.miguan.ballvideo.service.dsp.AdvertCodeService;
import com.miguan.ballvideo.service.dsp.AdvertDesignService;
import com.miguan.ballvideo.service.dsp.AdvertGroupService;
import com.miguan.ballvideo.vo.AdvertGroupListVo;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.jsoup.select.Collector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计划组serviceImpl
 */
@Slf4j
@Service
public class AdvertGroupServiceImpl implements AdvertGroupService {

    @Resource
    private AdvertGroupMapper advertGroupMapper;

    @Resource
    private AdvertCodeService advertCodeService;
    @Resource
    private AdvertDesignService advertDesignService;

    /**
     * 分页查询计划组列表
     * @param state 状态 状态：0-暂停，1-投放中
     * @param keyword
     * @param promotionPurpose 推广目的：1-应用推广，2-品牌推广
     * @param startDay         开始日期，格式：yyyy-MM-dd
     * @param endDay           结束日期，格式：yyyy-MM-dd
     * @param endDay           排序字段
     * @param pageNum          页码
     * @param pageSize         每页记录数
     * @return
     */
    public PageInfo<AdvertGroupListVo> pageAdvertGroupList(Integer state, String keyword, Integer promotionPurpose, String startDay, String endDay, String sort,
                                                           Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        params.put("keyword", keyword);
        params.put("promotionPurpose", promotionPurpose);
        params.put("startDay", startDay);
        params.put("endDay", endDay);
        params.put("sort", StringUtils.isEmpty(sort) ? "id desc" : StringUtil.humpToLine(sort));

        PageHelper.startPage(pageNum, pageSize);
        Page<AdvertGroupListVo> pageResult = advertGroupMapper.findAdvertGroupList(params);
        return new PageInfo(pageResult);
    }

    /**
     * 根据id查询计划组信息
     *
     * @param id
     * @return
     */
    public AdvertGroupVo getAdvertGroupById(int id) {
        return advertGroupMapper.getAdvertGroupById(id);
    }

    /**
     * 新增或修改计划组
     *
     * @param advertGroupVo 计划组信息
     */
    public AdvertGroupVo saveGroup(AdvertGroupVo advertGroupVo) {
        if (advertGroupVo == null) {
            throw new CommonException(400, "计划组信息为空");
        }
        if (advertGroupVo.getId() == null) {
            //新增操作
            advertGroupMapper.insertAdvertGroup(advertGroupVo);
        } else {
            //修改操作
            advertGroupMapper.updateAdvertGroup(advertGroupVo);
        }
        return advertGroupVo;
    }

    /**
     * 删除计划组
     *
     * @param id
     */
    public void deleteGroup(Integer id) {
        advertGroupMapper.deleteGroup(id);  //删除计划组
        advertGroupMapper.deletePlanByGroupId(id);  //删除计划组下的计划
        advertGroupMapper.deleteDesignByGroupId(id);  //删除计划组下的创意
        advertGroupMapper.deleteDesWeightByGroupId(id);  //删除计划组下的创意和计划的关联关系
        //删除设计 需要删除缓存
        List<Integer> groupIds = Lists.newArrayList();
        groupIds.add(id);
        List<AdvertCodeVo> codeVos = advertCodeService.findAdvCodeByGroupIds(groupIds);
        advertDesignService.removeAdvCacheByCodes(codeVos);
    }

    /**
     * 计划组批量上线 和 批量下线
     *
     * @param state 类型，1--批量上线，0--批量下线
     * @param ids   计划组id，多个逗号分隔
     */
    public void batchOnlineAndUnderline(int state, String ids) {
        if (StringUtils.isBlank(ids)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();

        List<String> idList = Arrays.asList(ids.split(","));
        params.put("idList", idList);
        params.put("state", state);
        advertGroupMapper.updateGroupState(params);  //批量修改计划组的状态
        advertGroupMapper.updatePlanState(params);  //批量修改计划的状态
        advertGroupMapper.updateDesignState(params);  //批量修改创意的状态
        //删除设计 需要删除缓存
        List<AdvertCodeVo> codeVos = advertCodeService.findAdvCodeByGroupIds(idList.stream().collect(Collectors.mapping(Integer::valueOf,Collectors.toList())));
        advertDesignService.removeAdvCacheByCodes(codeVos);
    }

    @Override
    public List<AdvertGroupVo> getGroupList(Long advertUserId) {
        return advertGroupMapper.getGroupList(advertUserId);
    }


}
