package com.miguan.reportview.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.reportview.entity.VideoSta;
import com.miguan.reportview.vo.VideoStaVo;

import java.util.List;

/**
 * 统计视频进文量和下线量服务接口
 *
 * @author zhongli
 * @since 2020-08-07 20:35:51
 * @description
 */
public interface IVideoStaService extends IService<VideoSta> {

    List<VideoStaVo> getData(String startDate,
                             String endDate,
                             boolean isGroupByCatId);
}
