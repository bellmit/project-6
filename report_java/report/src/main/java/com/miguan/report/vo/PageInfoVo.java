package com.miguan.report.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageInfoVo<T> {
    private int pageNum;
    private int pageSize;
    private int size;
    private int startRow;
    private int endRow;
    private int pages;
    private int prePage;
    private int nextPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private long total;
    private List<T> datalist;

}
