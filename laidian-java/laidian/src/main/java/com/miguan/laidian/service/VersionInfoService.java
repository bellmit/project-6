package com.miguan.laidian.service;

import com.miguan.laidian.vo.ClUserVersion;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface VersionInfoService {


    Map<String, Object> findSysVersionInfoAndUserId(String appType, ClUserVersion clUserVersion);

    int addUserVersionInfo(String appType,ClUserVersion clUserVersion);
}
