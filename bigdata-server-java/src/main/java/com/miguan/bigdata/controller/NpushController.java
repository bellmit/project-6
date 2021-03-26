package com.miguan.bigdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.RabbitMqConstants;
import com.miguan.bigdata.common.constant.SymbolConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Api(value = "迭代推测试接口", tags = {"迭代推测试接口"})
@RequestMapping("/api/npush")
@RestController
public class NpushController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "推送设备初始化")
    @PostMapping("/distinctInit")
    public String distinctInit(@ApiParam(value = "日期时间:yyyy-MM-dd") String date) {
        if (StringUtils.isEmpty(date)) {
            return "设备初始化日期不能为空,日期格式：yyyy-MM-dd, 多个日期逗号间隔";
        }

        String[] days = date.split(SymbolConstants.comma);
        for (String day : days) {
            JSONObject init = new JSONObject();
            init.put("type", "npush_init_of_day");
            init.put("day", day);
            rabbitTemplate.convertAndSend(RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_EXCHANGE, RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_RUTEKEY, init.toJSONString());
        }
        return "处理中";
    }

    @ApiOperation(value = "最近几天推送设备初始化")
    @PostMapping("/distinctInit/lastFewDays")
    public String distinctInitLastFewDays(@ApiParam(value = "天数") Integer lastFewDays) {
        if (lastFewDays == null || lastFewDays < 1) {
            return "天数不能为空,且大于0";
        }

        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < lastFewDays; i++) {
            String day = localDate.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
            JSONObject init = new JSONObject();
            init.put("type", "npush_init_of_day");
            init.put("day", day);
            rabbitTemplate.convertAndSend(RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_EXCHANGE, RabbitMqConstants.BIGDATA_POINT_NPUSH_INIT_RUTEKEY, init.toJSONString());
        }
        return "处理中";
    }
}
