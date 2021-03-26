package com.miguan.xuanyuan.mapper.common;

import java.util.List;

public interface BaseMapper<T> {
    int insert(T t);
    int update(T t);
    T findById(Long id);
    List<T> findAll();
}
