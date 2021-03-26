package com.miguan.bigdata.controller.base;

import com.miguan.bigdata.common.util.AmpFiled;
import com.miguan.bigdata.dto.DisassemblyChartDto;
import com.miguan.bigdata.dto.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.IterableUtils.contains;

/**
 * @author zhongli
 * @date 2020-07-30
 */
@Slf4j
public abstract class BaseController {
    public static final String SEQ = ",";
    public static final String NAME_SEQ = "-";

    public <T> ResponseEntity<T> success(T obj) {
        return ResponseEntity.success(obj);
    }

    public ResponseEntity success() {
        return ResponseEntity.success();
    }

    public ResponseEntity fail(Supplier<String> msg) {
        return ResponseEntity.error(400, msg.get());
    }

    public List<String> seqArray2List(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] arrayS = str.split(SEQ);
        return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
    }

    public List<Integer> seqArray3List(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] arrayS = str.split(SEQ);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arrayS.length; i++) {
            list.add(Integer.parseInt(arrayS[i]));
        }
        return list;
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> exceptionHandler(Throwable ex) {
        log.error(ex.getMessage(), ex);
        String eer = Stream.of(ex.getStackTrace()[0]).map(e -> e.toString()).collect(Collectors.joining("\\r\\n"));
        ResponseEntity<String> resp = ResponseEntity.error(400, ex.getMessage());
        resp.setData(eer);
        return resp;
    }

    /**
     * 导出排除字段/属性
     *
     * @param groups
     * @return
     */
    public String[] poiExclusions(List<String> groups, List<String> groupNames) {
        return groups.stream().map(e -> {
            int index = Integer.parseInt(e) - 1;
            return groupNames.get(index);
        }).toArray(String[]::new);
    }

    /**
     * 导出排除字段/属性
     *
     * @param showTypeParam
     * @param amp
     * @return
     */
    public String[] poiExclusions(String showTypeParam, Map<Integer, AmpFiled> amp) {
        if (StringUtils.isBlank(showTypeParam)) {
            showTypeParam = "-1";
        }
        String[] showTypes = showTypeParam.split(",");
        Map<Integer, AmpFiled> fieldMap = new HashMap<>(amp);
        for (int i = 0; i < showTypes.length; i++) {
            int showType = Integer.parseInt(showTypes[i]);
            fieldMap.remove(showType);
        }
        List<String> list = new ArrayList<>();
        fieldMap.forEach((key, value) -> {
            list.add(value.getName());
        });
        return list.toArray(new String[list.size()]);
    }

    /**
     * 合并数组
     *
     * @param exclusions1
     * @param Exclusions2
     * @return
     */
    public String[] mergeExclusions(String[] exclusions1, String[] Exclusions2) {
        List<String> list = new ArrayList(Arrays.asList(exclusions1));
        list.addAll(Arrays.asList(Exclusions2));
        return list.toArray(new String[list.size()]);
    }

    public List<DisassemblyChartDto> top10(List<DisassemblyChartDto> chartData) {
        DisassemblyChartDto op = chartData.stream().max(Comparator.comparing(DisassemblyChartDto::getDate)).get();
        String date = op.getDate();
        List<String> top = chartData.stream().filter(e -> e.getDate().equals(date)).sorted(Comparator.comparing(DisassemblyChartDto::getValue).reversed())
                .map(DisassemblyChartDto::getType).limit(10).collect(Collectors.toList());
        return chartData.stream().filter(e -> contains(top, e.getType())).collect(Collectors.toList());
    }
}
