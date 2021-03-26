package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.LockScreen;
import com.miguan.laidian.repositories.LockScreenDao;
import com.miguan.laidian.service.LockScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 锁表壁纸ServiceImpl
 *
 * @Author xy.chen
 **/
@Slf4j
@Service("lockScreenService")
public class LockScreenServiceImpl implements LockScreenService {

    @Resource
    private LockScreenDao lockScreenDao;

    @Override
    public List<LockScreen> findLockScreenList() {
        return lockScreenDao.findLockScreenList();
    }

    @Override
    public boolean updateLockScreen(Long id) {
        boolean flag = false;
        try {
            int num = lockScreenDao.updateLockScreen(id);
            if (num == 1) {
                flag = true;
            }
        } catch (Exception e) {
            log.info("更新壁纸设置次数失败,[{}]" + e.getMessage());
        }
        return flag;
    }

}
