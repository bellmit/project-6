package com.miguan.xuanyuan.common.security.context;

import com.miguan.xuanyuan.common.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Value;

public class UserContext {


    @Value("${spring.profiles.active}")
    private static String env;

    //把构造函数私有化，外部不能new
    private UserContext() {

    }

    private static final ThreadLocal<JwtUser> context = new ThreadLocal<JwtUser>();

    /**
     * 存放用户信息
     *
     * @param user
     */
    public static void set(JwtUser user) {
        context.set(user);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static JwtUser get() {
        return context.get();
    }

    /**
     * 清除当前线程内引用，防止内存泄漏
     */
    public static void remove() {
        context.remove();
    }
}