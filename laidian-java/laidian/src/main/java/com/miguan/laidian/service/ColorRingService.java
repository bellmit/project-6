package com.miguan.laidian.service;

import com.miguan.laidian.common.util.ResultMap;

/**
 * 对接讯飞的彩铃视频Service
 * @author suhj
 * @date 2020-05-07
 */
public interface ColorRingService {

    /**
     * 彩铃栏目资源查询，查询栏目下的子栏目信息
     * @param colId 栏目编号:彩铃栏目ID-318641;视频栏目ID-318633
     * @param operator 运营商类型:1-中国移动;2-中国联通;2-中国联通;
     * @return
     */
    ResultMap getQCols(String colId, String operator);

    /**
     * 查询栏目下铃音资源
     * @param colId 栏目编号
     * @param currentPage 页码
     * @param pageSize 单页数据数量
     * @param operator 运营商类型:1-中国移动;2-中国联通;2-中国联通;
     * @return
     */
    ResultMap getQColRes(String colId, String currentPage, String pageSize, String operator);

    /**
     * 查询栏目下视频
     * @param colId 栏目编号
     * @param currentPage 页码
     * @param pageSize 单页数据数量
     * @param operator 运营商类型:1-中国移动;2-中国联通;2-中国联通;
     * @return
     */
    ResultMap getQColResVr(String colId, String currentPage, String pageSize, String operator);

    /**
     * 查铃音搜索词
     * @param showSize 数据数量
     * @return
     */
    ResultMap getQSkw(String showSize);

    /**
     * 查视频搜索词
     * @param showSize 数据数量
     * @return
     */
    ResultMap getQSkwVr(String showSize);

    /**
     * 搜索铃音
     * @param keyword 关键词
     * @param singer 歌手
     * @param song 歌名
     * @param currentPage 页码
     * @param pageSize 单页数据数量
     * @param operator 运营商类型:1-中国移动;2-中国联通;2-中国联通;
     * @return
     */
    ResultMap getSearch(String keyword, String singer, String song,
                                  String currentPage, String pageSize, String operator);

    /**
     * 搜索视频
     * @param keyword 关键词
     * @param currentPage 页码
     * @param pageSize 单页数据数量
     * @param operator 运营商类型:1-中国移动;2-中国联通;2-中国联通;
     * @return
     */
    ResultMap getSearchVr(String keyword,
                                  String currentPage, String pageSize, String operator);
}
