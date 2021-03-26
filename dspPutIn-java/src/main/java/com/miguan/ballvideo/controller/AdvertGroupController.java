package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.service.dsp.AdvertGroupService;
import com.miguan.ballvideo.vo.AdvertGroupListVo;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Api(value="Dsp自投平台计划组Controller",tags={"计划组接口"})
@RestController
public class AdvertGroupController {

    @Resource
    private AdvertGroupService advertGroupService;

    @ApiOperation("分页查询计划组列表")
    @PostMapping("/api/dsp/advertGroup/pageAdvertGroupList")
    public PageInfo<AdvertGroupListVo> pageAdvertGroupList(@ApiParam("状态 状态：0-暂停，1-投放中") Integer state,
                                                           @ApiParam("计划组id/名称") String keyword,
                                                           @ApiParam("推广目的：1-应用推广，2-品牌推广") Integer promotionPurpose,
                                                           @ApiParam("开始日期，格式：yyyy-MM-dd") String startDay,
                                                           @ApiParam("结束日期，格式：yyyy-MM-dd") String endDay,
                                                           @ApiParam("排序字段，字段+排序方式，例如：consume asc(花费按升序)，consume desc(花费按降序)") String sort,
                                                           @ApiParam(value="页码", required=true) Integer pageNum,
                                                           @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        return advertGroupService.pageAdvertGroupList(state,keyword, promotionPurpose, startDay, endDay, sort, pageNum, pageSize);
    }

    @ApiOperation("根据id获取计划组信息")
    @PostMapping("/api/dsp/advertGroup/getAdvertGroupById")
    public AdvertGroupVo getAdvertGroupById(@ApiParam("计划组Id") int id) {
        return advertGroupService.getAdvertGroupById(id);
    }

    @ApiOperation("获取计划组下拉列表")
    @GetMapping("/api/dsp/advertGroup/getGroupList")
    public List<AdvertGroupVo> getGroupList(@ApiParam("广告主id,暂时不用,等加了广告主权限后再用。") Long advertUserId) {
        return advertGroupService.getGroupList(advertUserId);
    }

    @ApiOperation("计划组新增和修改接口（id为空则是新增，不为空则是修改）")
    @PostMapping("/api/dsp/advertGroup/save")
    public AdvertGroupVo getAdvCode(AdvertGroupVo advertGroupVo) {
        return advertGroupService.saveGroup(advertGroupVo);
    }

    @ApiOperation("删除计划组")
    @PostMapping("/api/dsp/advertGroup/delete")
    public void deleteGroup(@ApiParam("计划组id") Integer id) {
        advertGroupService.deleteGroup(id);
    }

    @ApiOperation("计划组批量上线或下线")
    @PostMapping("/api/dsp/advertGroup/batchOnlineAndUnderline")
    public void batchOnlineAndUnderline(@ApiParam("类型，1--批量上线，0--批量下线") int state,
                                        @ApiParam("计划组id，多个逗号分隔") String ids) {
        advertGroupService.batchOnlineAndUnderline(state, ids);
    }
}
