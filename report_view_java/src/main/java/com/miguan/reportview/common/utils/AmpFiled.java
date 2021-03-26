package com.miguan.reportview.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class AmpFiled {
    private int position;
    private String notes;
    private String name;
    private Field field;
}
