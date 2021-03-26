package com.miguan.ballvideo.service;

import java.util.List;

public interface FindVideosService {

    List<String> getVideoId(String showedVideoIds, Integer catid, int count, List<Integer> excludeCollectids);

}
