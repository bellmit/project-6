package com.miguan.ballvideo.common.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * 获取yml文件内容
 * @author xy.chen
 * @date 2019-6-28
 */
public class YmlUtil {
    private static String PROPERTY_NAME = "application.yml";

    public static String getCommonYml(Object key) {
        Resource resource = new ClassPathResource(PROPERTY_NAME);
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties = yamlFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties.get(key) == null ? "": properties.get(key).toString();
    }

    public static void main(String[] args) {
        Object yml = getCommonYml("spring.redis.expiretime");
        System.out.println(yml);
    }
}