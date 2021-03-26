package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.request.AccountUpdateVo;
import com.miguan.advert.domain.vo.result.AccountDetailInfoVo;
import com.miguan.advert.domain.vo.result.AccountInfoVo;

import java.util.List;
import java.util.Map;

public interface AccountMapper {

    /**
     * 查询账号列表
     * @return
     */
    List<AccountInfoVo> getAccountList(Map<String, Object> param);

    /**
     * 查询账号详细信息
     * @return
     */
    List<AccountDetailInfoVo> getAccountDetailList(Map<String, Object> param);

    /**
     * 保存账号信息
     * @param vo
     * @return
     */
    int addAccountInfo(AccountDetailInfoVo vo);

    int updateAccountInfo(AccountDetailInfoVo vo);
}
