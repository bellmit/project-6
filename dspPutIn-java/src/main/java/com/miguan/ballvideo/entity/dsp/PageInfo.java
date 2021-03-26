package com.miguan.ballvideo.entity.dsp;

import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2020/11/24 10:48
 **/
@Data
public class PageInfo<T> {
    @ApiModelProperty("当前页码")
    private int current_page;

    @ApiModelProperty("一页多少条数据")
    private int per_page;

    @ApiModelProperty("总记录数")
    private long total;

    @ApiModelProperty("数据集合")
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

    @ApiModelProperty("最后页码")
    private Integer last_page;

    private Integer from;

    private Long to;

    public PageInfo(){

    }

    public PageInfo(int current_page, int per_page, long total, List<T> data) {
        this.current_page = current_page;
        this.per_page = per_page;
        this.total = total;
        this.data = data;
    }

    public PageInfo(com.github.pagehelper.Page<T> obj) {
        if(obj != null){
            this.current_page = obj.getPageNum();
            this.per_page = obj.getPageSize();
            this.total = obj.getTotal();
            this.data = obj.getResult();
        }
    }

    public PageInfo(Page<T> obj, String url) {
        if(obj != null){
            this.current_page = obj.getPageNum();
            this.per_page = obj.getPageSize();
            this.total = obj.getTotal();
            this.data = obj.getResult();
            int pageSize = (int)total/per_page + 1;
            this.from = (current_page - 1) * per_page + 1;
            this.to = current_page * per_page > total ? total : current_page * per_page;
            int nextPage = current_page + 1 > pageSize ? current_page : current_page + 1;
            this.first_page_url = url + "?page=1";
            this.last_page_url = url + "?page=" + pageSize;
            this.next_page_url = url + "?page=" + nextPage;
            this.per_page =per_page;
            this.path = url;
            this.last_page = pageSize;
            if (per_page > 1 && current_page>1) {
                this.prev_page_url = url + "?page=" + (current_page - 1);;
            }
        }
    }

}
