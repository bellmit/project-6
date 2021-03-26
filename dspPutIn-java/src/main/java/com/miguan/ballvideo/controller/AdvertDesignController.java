package com.miguan.ballvideo.controller;
import com.cgcg.context.util.StringUtils;
import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.ValidatorUtil;
import com.miguan.ballvideo.entity.dsp.PageInfo;
import com.miguan.ballvideo.service.dsp.AdvertDesignService;
import com.miguan.ballvideo.vo.request.AdvertDesignModifyVo;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import com.miguan.ballvideo.vo.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Api(value="Dsp广告创意(新) Controller",tags={"Dsp广告创意接口(新)"})
@RestController
@RequestMapping("/api/dsp/idea/AdvertDesign")
public class AdvertDesignController {

    @Autowired
    private AdvertDesignService advertDesignService;

    @ApiOperation("分页查询广告创意列表")
    @PostMapping("/pageList")
    public ResultMap<PageInfo<AdvertDesignListRes>> pageList(@ApiParam("广告主id") Integer advertUserId,
                                                              @ApiParam("广告创意id/名称") String keyword,
                                                              @ApiParam("所属广告计划") Integer planId,
                                                              @ApiParam("创意形式") Integer materialShape,
                                                              @ApiParam("开始日期，格式：yyyy-MM-dd") String startDay,
                                                              @ApiParam("结束日期，格式：yyyy-MM-dd") String endDay,
                                                              @ApiParam("排序字段，字段+排序方式，例如：consume asc(花费按升序)，consume desc(花费按降序)") String sort,
                                                              @ApiParam(value="页码", required=true) Integer pageNum,
                                                              @ApiParam(value="每页记录数", required=true) Integer pageSize) {
        return ResultMap.success(advertDesignService.pageList(keyword,advertUserId, planId, materialShape, startDay, endDay,sort , pageNum, pageSize));
    }

    @ApiOperation("新增、编辑广告创意多个")
    @PostMapping("/save")
    public ResultMap save(@ApiParam("广告创意实体类") @RequestBody AdvertDesignModifyVo advertDesignModifyVo) {
        try {
            if(advertDesignModifyVo == null){
                throw new ValidateException("请求错误,参数封装失败！");
            }
            if(advertDesignModifyVo.getGroup_id() == null){
                throw new ValidateException("必须传入计划组！");
            }
            if(advertDesignModifyVo.getPlan_id() == null){
                throw new ValidateException("必须传入广告计划！");
            }
            if(advertDesignModifyVo.getDesignList() == null || advertDesignModifyVo.getDesignList().size() == 0){
                throw new ValidateException("至少一个广告创意！");
            }
            if(advertDesignModifyVo.getMaterial_type() == null){
                throw new ValidateException("必须选择创意类型");
            }
            if(advertDesignModifyVo.getMaterial_shape() == null){
                throw new ValidateException("必须选择创意形式");
            } else if(StringUtils.isEmpty(MaterialShapeConstants.materialShapeMap.get(advertDesignModifyVo.getMaterial_shape()))){
                throw new ValidateException("创意形式参数有误！");
            }
            for (AdvertDesignVo designVo: advertDesignModifyVo.getDesignList()) {
                designVo.validate();
                //填充默认数据
                designVo.init();
                if(designVo.getId() == null && advertDesignModifyVo.getState() != null){
                    designVo.setState(advertDesignModifyVo.getState());
                }
                if(designVo.getMaterial_type() == null || designVo.getMaterial_type() == -1){
                    Integer materialType = MaterialShapeConstants.oldMaterialTypeMap.get(advertDesignModifyVo.getMaterial_shape());
                    designVo.setMaterial_type(materialType);
                }
            }
            //添加创意
            advertDesignService.saveBatch(advertDesignModifyVo);
            return ResultMap.success();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("获取广告创意下拉列表")
    @GetMapping("/getDesignList")
    public ResultMap<List<AdvertDesignSimpleRes>> getDesignList(@ApiParam("广告主id") Long advertUserId) {
        // todo advert_user_id 校验还原
//        if(advertUserId == null){
//            ResultMap.error("必须传入广告主");
//        }
//        if(groupId == null){
//            ResultMap.error("必须传入计划组");
//        }
        return ResultMap.success(advertDesignService.getDesignList(advertUserId));
    }

    @ApiOperation("查询广告创意信息")
    @PostMapping("/info")
    public ResultMap<AdvertDesignModifyRes> info(@ApiParam("广告创意实体类") Long designId) {
        try {
            if(designId == null){
                return ResultMap.error("必须传入广告创意");
            }
            //获取创意信息
            AdvertDesignModifyRes designRes = advertDesignService.findResById(designId);
            return ResultMap.success(designRes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

    @ApiOperation("启用或者关闭")
    @PostMapping("/changeState")
    public void changeState(@ApiParam("状态，0-关闭，1开启") int state,
                                        @ApiParam("广告创意id") Long id) {
        advertDesignService.changeState(state, id);
    }

    @ApiOperation("删除广告创意")
    @GetMapping("/delete")
    public ResultMap delete(@ApiParam("广告创意的id") Long designId) {
        try {
            if(designId == null){
                return ResultMap.error("必须传入广告创意");
            }
            advertDesignService.delete(designId);
            return ResultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
