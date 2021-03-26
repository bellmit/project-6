package com.miguan.bigdata.common.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SpringProfiles {
    @Value("${spring.profiles.active}")
    private String active;

    public boolean isLocal(){
        return StringUtils.equals("local", active);
    }

    public boolean isLocalDev(){
        return StringUtils.equals("localdev", active);
    }

    public boolean isProd(){
        return StringUtils.equals("prod", active);
    }
}
