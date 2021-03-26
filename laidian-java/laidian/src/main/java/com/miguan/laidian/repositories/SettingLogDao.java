package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.AuthoritySettingErrlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingLogDao extends JpaRepository<AuthoritySettingErrlog, Long> {


}
