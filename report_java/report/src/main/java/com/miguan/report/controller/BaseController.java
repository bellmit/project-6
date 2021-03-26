package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.dto.DisassemblyChartDto;
import com.miguan.report.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

import static com.miguan.report.common.constant.CommonConstant.SUM_NAME;

/**base controll
 * 封装一些通用操作方法
 * @author zhongli
 * @date 2020-06-22 
 *
 */
@Slf4j
public abstract class BaseController {

    public <T> ResponseDto<T> success(T object) {
        if (object == null) {
            return successForNotData();
        }
        return new ResponseDto(200, "操作成功", object);
    }

    public ResponseDto fail(String message) {
        return new ResponseDto(400, message, null);
    }

    public ResponseDto successForNotData() {
        return new ResponseDto(200, "未找到数据", null);
    }


    public void sort(List<DisassemblyChartDto> list) {
        list.sort(Comparator.comparing(DisassemblyChartDto::getDate).thenComparingInt(e -> getSortNum(e.getType())));
    }

    public int getSortNum(String name) {
        if (name.startsWith(SUM_NAME)) {
            return 0;
        }
        if (name.startsWith(CommonConstant.APP_XY)) {
            return 1;
        }
        if (name.startsWith(CommonConstant.APP_GG)) {
            return 2;
        }
        return 3;
    }
}
