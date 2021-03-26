package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.ChannelCostDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.entity.FatherChannel;
import com.miguan.reportview.service.IChannelCostService;
import com.miguan.reportview.service.IChannelManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 父渠道成本管理
 */
@Api(value = "父渠道成本管理", tags = {"渠道成本"})
@RestController
public class ChannelManageController extends BaseController {

    @Autowired
    private IChannelManageService channelManageService;
    @Autowired
    private IChannelCostService channelCostService;


    @ApiOperation(value = "父渠道管理-列表")
    @PostMapping("api/channel_manage/list")
    public ResponseEntity<Map<String,Object>> listFatherChannel(@ApiParam(value = "父渠道对象")  @RequestBody Map<String, Object> params) {
        Object fatherChannel=params.get("fatherChannel");
        if (fatherChannel!=null){
            params.put("fatherChannel",fatherChannel.toString().replace("_","\\_"));
        }
        List<FatherChannel> list = channelManageService.listFatherChannel(params);
        int count = channelManageService.getFatherChannelCount(params);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("result",list);
        map.put("count",count);
        return success(map) ;
    }



    @ApiOperation(value = "父渠道管理-新增")
    @PostMapping("api/channel_manage/add")
    public ResponseEntity saveFatherChannel(@ApiParam(value = "父渠道对象", required = true) @RequestBody FatherChannel fatherChannelDO){
        try {
            fatherChannelDO.setStatus(1);
            if(channelManageService.addFatherChannel(fatherChannelDO)>0){
                channelCostService.addFatherChannelCost(fatherChannelDO);
                return success();
            }
            return fail(() -> "操作出错，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(() -> "操作出错，请联系管理员"+e.getCause().getMessage());
        }
    }


    @ApiOperation(value = "父渠道管理-更新")
    @PostMapping("api/channel_manage/update")
    public ResponseEntity updateFatherChannel(@ApiParam(value = "父渠道对象", required = true) @RequestBody FatherChannel fatherChannelDO){
        try {
            Long channelId=fatherChannelDO.getId();
            String fatherChannel=fatherChannelDO.getFatherChannel();
            FatherChannel oldChannelDO = channelManageService.getFatherChannel(channelId);
            int newStatus = fatherChannelDO.getStatus();
            int oldStatus = oldChannelDO.getStatus();
            //停用渠道的要设置时间
            String statusTime="";
            if(newStatus==0 && oldStatus==1){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                statusTime=df.format(new Date());
                fatherChannelDO.setStatusTime(statusTime);
            }

            if(channelManageService.updateFatherChannel(fatherChannelDO)==0){
                return fail(() -> "操作出错，请联系管理员");
            }
            //启用渠道的要看下有没有当天的记录，没有则新增一条
            if(newStatus==1 && oldStatus==0){
                Map<String, Object> map=new HashMap<String, Object>();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String yesterday =df.format(new Date(System.currentTimeMillis()-1000*60*60*24));
                map.put("startDate",yesterday);
                map.put("endDate",yesterday);
                map.put("channelIds",fatherChannel.split(","));
                int count=channelCostService.getChannelCostCount(map);
                if(count==0){
                    channelCostService.addFatherChannelCost(fatherChannelDO);
                }
            }
            return success();

        } catch (Exception e) {
            e.printStackTrace();
            return fail(() -> "操作出错，请联系管理员"+e.getCause().getMessage());
        }
    }

    @ApiOperation(value = "父渠道管理-删除")
    @GetMapping("api/channel_manage/delete")
    public ResponseEntity deleteFatherChannel(@ApiParam(value = "父渠道id", required = true)  Long id){
        try {
            if(channelManageService.deleteFatherChannel(id)>0){
                return success();
            }
            return fail(() -> "操作出错，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(() -> "操作出错，请联系管理员"+e.getCause().getMessage());
        }
    }

    @ApiOperation(value = "渠道成本-列表")
    @PostMapping("api/channel_cost/list")
    public ResponseEntity<Map<String,Object> > listCost(@RequestBody Map<String, Object> params) {
        Object appPackages = params.get("appPackages");
        if(appPackages!=null){
            params.put("appPackages", isBlank(appPackages.toString()) ? null : appPackages.toString().split(","));
        }
        Object channelIds = params.get("channelIds");
        if(channelIds!=null){
            params.put("channelIds", isBlank(channelIds.toString()) ? null : channelIds.toString().split(","));
        }
        Object groups = params.get("groups");
        if(groups!=null){
            params.put("groups", isBlank(groups.toString()) ? null : groups.toString().split(","));
        }
        List<ChannelCostDto> list = channelCostService.list(params);
        int count = channelCostService.getChannelCostCount(params);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("result",list);
        map.put("count",count);
        return success(map) ;
    }

    @ApiOperation(value = "渠道成本-更新成本")
    @PostMapping("api/channel_cost/updateCost")
    public ResponseEntity<List<ChannelCostDto>> updateCost(@RequestBody Map<String, Object> params) {

        try {
            if(channelCostService.updateCost(params)>0){
                return success();
            }
            return fail(() -> "操作出错，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(() -> "操作出错，请联系管理员"+e.getCause().getMessage());
        }

    }


    @ApiOperation(value = "渠道成本数据导出")
    @PostMapping("api/channel_cost/export")
    public void ldExport(HttpServletResponse response,
                         @RequestBody Map<String, Object> params) throws IOException {

        Object appPackages = params.get("appPackages");
        if(appPackages!=null){
            params.put("appPackages", isBlank(appPackages.toString()) ? null : appPackages.toString().split(","));
        }
        Object channelIds = params.get("channelIds");
        if(channelIds!=null){
            params.put("channelIds", isBlank(channelIds.toString()) ? null : channelIds.toString().split(","));
        }
        Object groups = params.get("groups");
        if(groups!=null){
            params.put("groups", isBlank(groups.toString()) ? null : groups.toString().split(","));
        }
        List<ChannelCostDto> list = channelCostService.list(params);

        ExportParams excelParams = new ExportParams("渠道成本数据", "渠道成本数据", ExcelType.XSSF);
        //导出排除字段/属性
//        List<String> groups = seqArray2List(groupType);
//        params.setExclusions(mergeExclusions(exclusions1, exclusions2));
        ExcelUtils.defaultExport(list, ChannelCostDto.class, "渠道成本数据", response, excelParams);
    }





    @ApiOperation(value = "渠道成本-初始化昨天数据")
    @GetMapping("api/channel_cost/dateCostGenerate")
    public ResponseEntity  dateCostGenerate() {

        try {
            if(channelCostService.dateCostGenerate()>0){
                return success();
            }
            return fail(() -> "操作出错，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(() -> "操作出错，请联系管理员"+e.getCause().getMessage());
        }

    }



    @ApiOperation(value = "父渠道管理-友盟列表")
    @GetMapping("api/channel_manage/ym_list")
    public ResponseEntity listFatherChannelYm() {
        List<String> list = channelManageService.listFatherChannelYm();
        return success(list) ;
    }

    @ApiOperation(value = "父渠道管理-负责人列表")
    @GetMapping("api/channel_manage/owner_list")
    public ResponseEntity listOwner() {
        List<String> list = channelManageService.listOwner();
        return success(list) ;
    }




}
