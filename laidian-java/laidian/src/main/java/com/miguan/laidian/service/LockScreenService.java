package com.miguan.laidian.service;

import com.miguan.laidian.entity.LockScreen;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 锁表壁纸Service
 *
 * @Author xy.chen
 **/
public interface LockScreenService {

    /**
     * 随机获取5个壁纸返回
     *
     * @return
     */
    List<LockScreen> findLockScreenList();


    /**
     * 根据ID更新壁纸设置默认次数
     *
     * @param id
     * @return
     */
    @Transactional
    boolean updateLockScreen(Long id);

}
