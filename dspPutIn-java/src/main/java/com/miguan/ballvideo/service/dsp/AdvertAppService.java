package com.miguan.ballvideo.service.dsp;


import com.miguan.ballvideo.vo.response.AdvertAppSimpleRes;

import java.util.List;

/**
 * app
 */
public interface AdvertAppService {
    List<AdvertAppSimpleRes> getAppList(Integer materialShape, String materialType);
}
