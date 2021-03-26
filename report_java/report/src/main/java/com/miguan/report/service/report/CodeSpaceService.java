package com.miguan.report.service.report;

import com.miguan.report.vo.AdErrorVo;
import com.miguan.report.vo.CodeSpaceVo;
import com.miguan.report.vo.PageInfoVo;

import java.util.List;

public interface CodeSpaceService {

    /**
     * 获取代码位分析列表(导出功能)
     *
     * @param startDdate
     * @param endDdate
     * @param packageNames
     * @param clientType
     * @param codeSpaceNames
     * @param serialNums
     * @return
     */
    public List<CodeSpaceVo> getAnalyzeListNew(String reportType, String startDdate, String endDdate,
                                               List<String> packageNames, List<Integer> clientType,
                                               List<String> codeSpaceNames, List<Integer> serialNums,
                                               int pageNum, int pageSize);

    /**
     * 获取代码位分析列表(展示列表)
     * @param reportType
     * @param startDdate
     * @param endDdate
     * @param packageNames
     * @param clientType
     * @param codeSpaceNames
     * @param serialNums
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfoVo<CodeSpaceVo> getAnalyzeListNew4Page(String reportType, String startDdate, String endDdate,
                                             List<String> packageNames, List<Integer> clientType,
                                             List<String> codeSpaceNames, List<Integer> serialNums,
                                             int pageNum, int pageSize);

    /**
     * 获取代码位错误明细
     *
     * @param date      日期
     * @param codeSpace 代码位
     * @return
     */
    public List<AdErrorVo> getCodeSpaceErrorDetail(String detailType, String date, String codeSpace);
}
