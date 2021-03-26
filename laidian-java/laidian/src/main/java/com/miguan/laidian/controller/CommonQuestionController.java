package com.miguan.laidian.controller;


import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.CommonQuestion;
import com.miguan.laidian.service.CommonQuestionService;
import com.miguan.laidian.vo.CommonQuestionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "常见问题controller", tags = {"常见问题接口"})
@RequestMapping("/api/commonQuestion")
@RestController
public class CommonQuestionController {

    @Resource
    CommonQuestionService commonQuestionService;

    /**
     * 常见问题列表
     * HYL 2019年11月5日14:36:17
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "常见问题列表")
    @PostMapping("/findAllCommonQuestion")
    public ResultMap findAllCommonQuestion(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                           @ModelAttribute CommonQuestion commonQuestion){
        Map<String, Object> restMap = new HashMap<>();
        int currentPage = commomParams.getCurrentPage();
        int pageSize = commomParams.getPageSize();
        List<CommonQuestion> allCommonQuestion = commonQuestionService.findAllCommonQuestion(commonQuestion,currentPage,pageSize);
        if(allCommonQuestion==null){
            return ResultMap.error("数据库添加设备不全");
        }
        restMap.put("data",allCommonQuestion);
        return ResultMap.success(restMap);
    }

    /**
     * 修改问题查看数和点击数
     *  HYL 2019年11月5日14:36:12
     * @param
     * @param
     * @return
     */
    @ApiOperation(value = "修改问题查看数和点击数")
    @PostMapping("/updateCommonQuestionNumber")
    public ResultMap updateCommonQuestionNumber(@ModelAttribute CommonQuestionVO commonQuestionVO){
        Map<String, Object> restMap = new HashMap<>();
        int i =  commonQuestionService.updateCommonQuestionNumber(commonQuestionVO);
        if(i>0){
            return ResultMap.success(restMap);
        }else {
            return ResultMap.error(restMap);
        }
    }

}
