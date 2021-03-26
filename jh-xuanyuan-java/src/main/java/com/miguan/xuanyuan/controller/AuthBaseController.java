package com.miguan.xuanyuan.controller;

import com.miguan.xuanyuan.common.security.context.UserContext;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthBaseController {

    public static JwtUser getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUser userDetails = null;
            if (authentication != null) {
                userDetails = (JwtUser)authentication.getPrincipal();
            }
            return userDetails;
        } catch (Exception e) {
            log.error("authentication转换异常", e);
            return new JwtUser(1L, "admin", "",  null);
        }
    }
}
