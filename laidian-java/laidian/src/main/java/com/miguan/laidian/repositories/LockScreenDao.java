package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.LockScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 锁表壁纸Dao
 *
 * @Author xy.chen
 **/
public interface LockScreenDao extends JpaRepository<LockScreen, Long> {

    @Query(value = "select * from lock_screen where state=1 ORDER BY RAND() limit 5", nativeQuery = true)
    List<LockScreen> findLockScreenList();

    @Modifying
    @Query(value = "update lock_screen set set_default_nums = set_default_nums + 1 where id = ?1", nativeQuery = true)
    int updateLockScreen(Long id);
}
