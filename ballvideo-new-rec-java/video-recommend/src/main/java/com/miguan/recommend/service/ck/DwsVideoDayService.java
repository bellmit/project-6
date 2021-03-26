package com.miguan.recommend.service.ck;

import java.util.List;

public interface DwsVideoDayService {

    public List<String> findTopVideo(Integer dt, Integer lowestShow);

    public List<String> findTopVideoWithCatId(Integer dt, Integer lowestShow, Integer catId);
}
