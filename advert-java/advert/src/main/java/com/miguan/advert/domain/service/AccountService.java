package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.request.AccountAddVo;
import com.miguan.advert.domain.vo.request.AccountUpdateVo;
import com.miguan.advert.domain.vo.result.AccountDetailInfoVo;
import com.miguan.advert.domain.vo.result.AccountInfoVo;
import com.miguan.advert.domain.vo.result.AccountListVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AccountService {

    AccountListVo getData(HttpServletRequest request, Integer page, Integer page_size, String name, String company_name, Integer status);

    AccountDetailInfoVo getInfo(Integer id);

    List<AccountInfoVo> getAccountList();

    int getAdd(HttpServletRequest request, AccountAddVo accountAddVo);

    int updateAccount(HttpServletRequest request, AccountUpdateVo updateVo);

}
