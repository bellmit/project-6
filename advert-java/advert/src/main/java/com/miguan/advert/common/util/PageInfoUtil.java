package com.miguan.advert.common.util;


import com.miguan.advert.domain.vo.result.PageResultVo;

/**
 * 分页Model
 *
 *@Author laiyd
 * @Date 2020/9/24
 */
public class PageInfoUtil {

	/**
	 * 获取翻页信息
	 * @param url 请求路径
	 * @param totalSize 总共多少条数据
	 * @param page 当前页数
	 * @param page_size 页面数量
	 * @return
	 */
	public static PageResultVo getPageInfo(String url, Integer totalSize, Integer page, Integer page_size) {
		PageResultVo pageResultVo = new PageResultVo();
		int pageSize = totalSize/page_size + 1;
		int from = (page - 1) * page_size + 1;
		int to = page * page_size > totalSize ? totalSize : page * page_size;
		int nextPage = page + 1 > pageSize ? 1 : page + 1;
		String firstPageUrl = url + "?page=1";
		String lastPageUrl = url + "?page=" + pageSize;
		String nextPageUrl = url + "?page=" + nextPage;
		String prevPageUrl = url + "?page=" + (page - 1);

		pageResultVo.setCurrent_page(page);
		pageResultVo.setLast_page(pageSize);
		pageResultVo.setPer_page(page_size);
		pageResultVo.setFrom(from);
		pageResultVo.setTo(to);
		pageResultVo.setTotal(totalSize);
		pageResultVo.setPath(url);
		pageResultVo.setFirst_page_url(firstPageUrl);
		pageResultVo.setLast_page_url(lastPageUrl);
		if (pageSize > 1) {
			pageResultVo.setNext_page_url(nextPageUrl);
		}
		if (page > 1) {
			pageResultVo.setPrev_page_url(prevPageUrl);
		}
		return pageResultVo;
	}
}
