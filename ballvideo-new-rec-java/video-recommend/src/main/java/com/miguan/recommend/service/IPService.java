package com.miguan.recommend.service;

import com.miguan.recommend.common.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

@Service
@Slf4j
public class IPService {
    @Resource
    private ResourceLoader resourceLoader;
    private DbSearcher searcher;

    @PostConstruct
    public void init() throws DbMakerConfigException, IOException {
        URL file = resourceLoader.getResource("classpath:ip/ip2region.db").getURL();
        byte[] bytes = IOUtils.toByteArray(file);
        searcher = new DbSearcher(new DbConfig(), bytes);
    }

    public String[] getCurrentIpInfo() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return getIpInfoByRequest(request);
    }

    public String[] getIpInfoByRequest(HttpServletRequest request) {
        String ip = request.getParameter("userIp");
        ip = StringUtils.isNoneBlank(ip) && IPUtil.isIpAddress(ip) ? ip : getIpAddr(request);
        return getIpInfo(ip);
    }

    public String[] getIpInfo(String ip) {
        if (StringUtils.isBlank(ip)) {
            return new String[]{"null", "null", "null", "null"};
        }
        DataBlock block = null;
        try {
            block = searcher.memorySearch(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (block == null) {
            return new String[]{ip, "null", "null", "null"};
        }
        String[] ipAdd = block.getRegion().split("\\|");
        String country = ipAdd[0];
        String province = ipAdd[2];
        String city = ipAdd[3];
        return new String[]{ip, country, province, city};
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    log.warn("获取客户端ip失败", e);
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}