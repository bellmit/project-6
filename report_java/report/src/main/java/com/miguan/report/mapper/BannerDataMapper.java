package com.miguan.report.mapper;

import com.github.pagehelper.Page;
import com.miguan.report.dto.BannerDataDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BannerDataMapper {

    public Page<BannerDataDto> findByDateAndTotalName(String startDdate, String endDdate, List<String> packageNames,
                                                      List<Integer> clientIds, List<String> totalNames, List<Integer> optionValues);

    public long findForCodeSpaceReportListCount(@Param("startDdate") String startDdate, @Param("endDdate") String endDdate, @Param("packageNames") List<String> packageNames,
                                                @Param("clientIds") List<Integer> clientIds, @Param("totalNames") List<String> totalNames, @Param("optionValues") List<Integer> optionValues);

    public List<BannerDataDto> findForCodeSpaceReportList(@Param("startDdate") String startDdate, @Param("endDdate") String endDdate, @Param("packageNames") List<String> packageNames,
                                                          @Param("clientIds") List<Integer> clientIds, @Param("totalNames") List<String> totalNames, @Param("optionValues") List<Integer> optionValues,
                                                          @Param("poffset") int poffset, @Param("psize") int psize);
}
