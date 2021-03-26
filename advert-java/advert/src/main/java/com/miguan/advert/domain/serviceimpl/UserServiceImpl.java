package com.miguan.advert.domain.serviceimpl;

import com.miguan.advert.common.util.PageInfoUtil;
import com.miguan.advert.domain.mapper.UserMapper;
import com.miguan.advert.domain.service.UserService;
import com.miguan.advert.domain.vo.result.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserListVo getData(HttpServletRequest request, Integer page, Integer page_size) {
        UserListVo userListVo = new UserListVo();
        List<UserDetailInfoVo> userDetailInfoVoList = userMapper.getUserList();
        if (CollectionUtils.isNotEmpty(userDetailInfoVoList)) {
            String url = request.getRequestURL().toString();
            PageResultVo pageResultVo = PageInfoUtil.getPageInfo(url, userDetailInfoVoList.size(), page, page_size);
            BeanUtils.copyProperties(pageResultVo, userListVo);
            List<UserDetailInfoVo> userDetailInfoVos = userDetailInfoVoList.subList((pageResultVo.getFrom() - 1), pageResultVo.getTo());
            userListVo.setData(userDetailInfoVos);
        }
        return userListVo;
    }

}
