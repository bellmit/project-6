package com.miguan.ballvideo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WebHotNewsUtil {

    /**
     * 获取热词
     * @param url 网址
     * @param className class属性名称
     * @param number 热词最大数
     * @return
     */
    public static List<String> getWebHotVideos(String url, String className, int number) {
        return patter_script(Analysis_page(get_page(url), className, number));
    }

    /**
     * 获取html
     * @param url
     * @return
     */
    public static Document get_page(String url){
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("url或网络连接错误!");
            return null;
        }
    }

    /**
     * 根据className过滤标签
     * @param page_html
     * @param className
     * @param number
     * @return
     */
    public static String Analysis_page(Document page_html, String className, int number){
        Elements links=page_html.getElementsByClass(className);
        int size = number;
        if (links.size() < number) {
            size = links.size();
        }
        StringBuffer allstring= new StringBuffer();
        for(int i = 0; i < size; i++) {
            String script=links.get(i).toString();
            allstring = allstring.append(script);
        }
        return allstring.toString();
    }

    /**
     * 提取热词
     * @param tag
     * @return
     */
    public static List<String> patter_script(String tag){
        List<String> result = new ArrayList<String>();
        if (StringUtils.isNotBlank(tag)) {
            String reg2 = "<a[^>]*>([^<]*)</a>";
            Matcher m = Pattern.compile(reg2).matcher(tag);
            while (m.find()) {
                String r = m.group(1);
                result.add(r);
            }
        } else {
            log.error("className错误,获取热词失败!");
        }
        return result;
    }
}
