package com.miguan.recommend.service.ck;

import java.util.List;
import java.util.Map;

public interface DwVideoActionService {

    public List<Map<String, Object>> findSimilarCatid(String date, String catid);
}
