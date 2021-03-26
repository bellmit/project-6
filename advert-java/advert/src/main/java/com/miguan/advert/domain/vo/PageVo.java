package com.miguan.advert.domain.vo;

import com.github.pagehelper.Page;
import com.miguan.advert.domain.vo.result.PageResultVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    private int current_page;
    private int page_size;
    private long total;
    private List<T> data;

    @ApiModelProperty("第一页url")
    private String first_page_url;

    @ApiModelProperty("最后一页url")
    private String last_page_url;

    @ApiModelProperty("下一页url")
    private String next_page_url;

    @ApiModelProperty("上一页url")
    private String prev_page_url;

    @ApiModelProperty("url")
    private String path;

    @ApiModelProperty("一页多少条数据")
    private Integer per_page;

    @ApiModelProperty("最后页码")
    private Integer last_page;

    private Integer from;

    private Long to;

    public PageVo(){

    }

    public PageVo(int current_page, int page_size, long total, List<T> data) {
        this.current_page = current_page;
        this.page_size = page_size;
        this.total = total;
        this.data = data;
    }

    public PageVo(Page<T> obj) {
        if(obj != null){
            this.current_page = obj.getPageNum();
            this.page_size = obj.getPageSize();
            this.total = obj.getTotal();
            this.data = obj.getResult();
        }
    }

    public PageVo(Page<T> obj,String url) {
        if(obj != null){
            this.current_page = obj.getPageNum();
            this.page_size = obj.getPageSize();
            this.total = obj.getTotal();
            this.data = obj.getResult();
            int pageSize = (int)total/page_size + 1;
            this.from = (current_page - 1) * page_size + 1;
            this.to = current_page * page_size > total ? total : current_page * page_size;
            int nextPage = current_page + 1 > pageSize ? current_page : current_page + 1;
            this.first_page_url = url + "?page=1";
            this.last_page_url = url + "?page=" + pageSize;
            this.next_page_url = url + "?page=" + nextPage;
            this.per_page =page_size;
            this.path = url;
            this.last_page = pageSize;
            if (page_size > 1 && current_page>1) {
                this.prev_page_url = url + "?page=" + (current_page - 1);;
            }
        }
    }

}
