package com.miguan.ballvideo.common.util.dsp;

import com.miguan.ballvideo.entity.dsp.Page;
import com.miguan.ballvideo.entity.dsp.PageVo;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @program: dspPutIn-java
 * @description: 分页工具类
 * @author: suhj
 * @create: 2020-09-23 15:32
 **/
public class PageUtil {

    public static Page setPageable(List<Map<String,Object>> pageList, PageVo pageVo){
        if(CollectionUtils.isEmpty(pageList)){
            return new Page(0, pageVo.getPage(), pageVo.getPage_size(),null);
        }
        Page page = new Page(pageList.size(), pageVo.getPage(), pageVo.getPage_size(),null);
        int length = page.getCurrent_page() * page.getPer_page();
        if(pageList.size() < length){
            length = pageList.size();
        }
        int currPage = (page.getCurrent_page()-1) * page.getPer_page();
        if(currPage <= length ){
            page.setData(pageList.subList(currPage,length));
        }
        return page;
    }

}
