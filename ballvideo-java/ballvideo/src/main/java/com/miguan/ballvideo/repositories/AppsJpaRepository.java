package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.Apps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppsJpaRepository extends JpaRepository<Apps,Long> {

    /**
     * 根据设备表id查询APP列表
     * @param deviceId 备表id
     * @return
     */
    int countAppsByDeviceId(Long deviceId);

    /**
     * 删除设备已存在的APP列表
     * @param deviceId
     */
    void deleteAppsByDeviceId(Long deviceId);
}
