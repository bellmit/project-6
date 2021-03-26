package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.vo.AdvertGroupListVo;
import com.miguan.ballvideo.vo.AdvertGroupVo;

import java.util.List;

/**
 * 计划组service
 */
public interface AdvertGroupService {

    /**
     * 分页查询计划组列表
     *
     * @param state            状态 状态：0-暂停，1-投放中
     * @param keyword
     * @param promotionPurpose 推广目的：1-应用推广，2-品牌推广
     * @param startDay         开始日期，格式：yyyy-MM-dd
     * @param endDay           结束日期，格式：yyyy-MM-dd
     * @param sort             排序字段
     * @param pageNum          页码
     * @param pageSize         每页记录数
     * @return
     */
    PageInfo<AdvertGroupListVo> pageAdvertGroupList(Integer state, String keyword, Integer promotionPurpose, String startDay, String endDay,String sort,
                                                    Integer pageNum, Integer pageSize);

    /**
     * 根据id查询计划组信息
     *
     * @param id
     * @return
     */
    AdvertGroupVo getAdvertGroupById(int id);

    /**
     * 新增或修改计划组
     *
     * @param advertGroupVo 计划组信息
     */
    AdvertGroupVo saveGroup(AdvertGroupVo advertGroupVo);

    /**
     * 删除计划组
     *
     * @param id
     */
    void deleteGroup(Integer id);

    /**
     * 计划组批量上线 和 批量下线
     *
     * @param state 类型，1--批量上线，0--批量下线
     * @param ids   计划组id，多个逗号分隔
     */
    void batchOnlineAndUnderline(int state, String ids);

    List<AdvertGroupVo> getGroupList(Long advertUserId);
}
