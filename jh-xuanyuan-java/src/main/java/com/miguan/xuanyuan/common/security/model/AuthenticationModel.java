package com.miguan.xuanyuan.common.security.model;

/**
 * @author yanlu
 * @date 2019/7/2  10:24
 * @describe
 */

public class AuthenticationModel {

    private String username;
    private String password;
    private String validateCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

}
