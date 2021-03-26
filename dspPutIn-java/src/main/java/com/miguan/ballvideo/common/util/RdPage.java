package com.miguan.ballvideo.common.util;

import com.github.pagehelper.Page;

/**
 * 分页Model
 *
 *@Author xy.chen
 * @Date 2019/7/9
 */
public class RdPage {
	
	/**
	 * 默认分页起始页面，第一页
	 */
	public static final int PAGE_NUM_DEFAULT = 1;
	
	/**
	 * 默认分页条数，10条
	 */
	public static final int PAGE_SIZE_DEFAULT = 12;

	/** 总数 */
	private long total;
	/** 总页数 */
	private int pages;
	/** 当前页数，从1开始 */
	private int current;
	/** 每页条数 */
	private int pageSize;

	public RdPage() {
		super();
	}

	/**
	 * 将pagehelper.page 赋值给 pages
	 * @param page
	 */
	public RdPage(Page<?> page) {
		this.total = page.getTotal();
		this.pages = page.getPages();
		this.current = page.getPageNum();
		this.pageSize = page.getPageSize();
	}

	/**
	 * 当前页是否有上一页
	 * 
	 * @return
	 */
	public boolean hasPre() {
		if (pages > 1 && current > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 当前页是否有下一页
	 * 
	 * @return
	 */
	public boolean hasNext() {
		if (pages > 1 && current < pages) {
			return true;
		}
		return false;
	}

	/**
	 * 获取总数
	 * @return total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 设置总数
	 * @param total
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * 获取总页数
	 * @return pages
	 */
	public int getPages() {
		return pages;
	}

	/**
	 * 设置总页数
	 * @param pages
	 */
	public void setPages(int pages) {
		this.pages = pages;
	}

	/**
	 * 获取页码，从1开始
	 * @return current
	 */
	public int getCurrent() {
		return current == 0 ? PAGE_NUM_DEFAULT : current;
	}

	/**
	 * 设置页码，从1开始
	 * @param current
	 */
	public void setCurrent(int current) {
		this.current = current;
	}
	/**
	 * 获取每页条数
	 * @return pageSize
	 */
	public int getPageSize() {
		return pageSize == 0 ? PAGE_SIZE_DEFAULT : pageSize;
	}

	/**
	 * 设置每页条数
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
