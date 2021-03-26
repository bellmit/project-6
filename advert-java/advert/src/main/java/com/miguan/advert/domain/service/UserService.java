package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.result.UserListVo;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    UserListVo getData(HttpServletRequest request, Integer page, Integer page_size);

}
