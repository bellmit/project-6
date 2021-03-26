package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.Git;

import java.util.List;
import java.util.Map;

public interface GiService {

    public List<Map<String, Object>> queryProductList(Map<String, Object> map);

    public List<Git> findAll();

    public void saveGit();
}
