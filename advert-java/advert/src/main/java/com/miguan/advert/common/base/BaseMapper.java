package com.miguan.advert.common.base;


import com.github.pagehelper.Page;

import java.util.List;

public interface BaseMapper<T> {

    int insert(T t);

    int updateByPrimaryKey(T t);

    void deleteByPrimaryKey(Long id);

    T findByPrimaryKey(Long id);

    List<T> findAll(T t);

    List<T> findSelective(T t);

    List<T> findByKeyword(T t);

    Page<T> findPage(T t);
}
