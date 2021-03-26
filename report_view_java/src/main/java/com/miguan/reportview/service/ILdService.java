package com.miguan.reportview.service;

import com.miguan.reportview.entity.VideosCat;
import com.miguan.reportview.vo.FirstVideosStaVo;

import java.util.List;

/**
 * <p>
 * 来电类别表 服务类
 * </p>
 *
 */
public interface ILdService {

   String getCatName(String catid);

    List<VideosCat> getVideosCat();
}
