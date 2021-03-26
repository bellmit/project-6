package com.miguan.advert.domain.serviceimpl;

import com.miguan.advert.common.util.PageInfoUtil;
import com.miguan.advert.domain.mapper.AccountMapper;
import com.miguan.advert.domain.service.AccountService;
import com.miguan.advert.domain.vo.request.AccountAddVo;
import com.miguan.advert.domain.vo.request.AccountUpdateVo;
import com.miguan.advert.domain.vo.result.AccountDetailInfoVo;
import com.miguan.advert.domain.vo.result.AccountInfoVo;
import com.miguan.advert.domain.vo.result.AccountListVo;
import com.miguan.advert.domain.vo.result.PageResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public AccountListVo getData(HttpServletRequest request, Integer page, Integer page_size, String name, String company_name, Integer status) {
        AccountListVo accountListVo = new AccountListVo();
        List<AccountDetailInfoVo> accountInfoList = getAccountDetailInfoVos(name, company_name, status);
        if (CollectionUtils.isNotEmpty(accountInfoList)) {
            String url = request.getRequestURL().toString();
            PageResultVo pageResultVo = PageInfoUtil.getPageInfo(url, accountInfoList.size(), page, page_size);
            BeanUtils.copyProperties(pageResultVo, accountListVo);
            List<AccountDetailInfoVo> accountInfoVos = accountInfoList.subList((pageResultVo.getFrom() - 1), pageResultVo.getTo());
            accountListVo.setData(accountInfoVos);
        }
        return accountListVo;
    }

    //查询账户信息
    private List<AccountDetailInfoVo> getAccountDetailInfoVos(String name, String company_name, Integer status) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotEmpty(name)) {
            name = "%" + name + "%";
            param.put("name", name);
        }
        if (StringUtils.isNotEmpty(company_name)) {
            company_name = "%" + company_name + "%";
            param.put("company_name", company_name);
        }
        if (status != null) {
            param.put("status", status);
        }
        return accountMapper.getAccountDetailList(param);
    }

    @Override
    public AccountDetailInfoVo getInfo(Integer id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        List<AccountDetailInfoVo> accountDetailInfoVos = accountMapper.getAccountDetailList(param);
        if (CollectionUtils.isNotEmpty(accountDetailInfoVos)) {
            return accountDetailInfoVos.get(0);
        }
        return null;
    }

    @Override
    public List<AccountInfoVo> getAccountList() {
        Map<String, Object> param = new HashMap<>();
        return accountMapper.getAccountList(param);
    }

    @Override
    public int getAdd(HttpServletRequest request, AccountAddVo accountAddVo) {
        AccountDetailInfoVo vo = new AccountDetailInfoVo();
        BeanUtils.copyProperties(accountAddVo, vo);
        setOperateUser(request, vo);
        try {
            int result = accountMapper.addAccountInfo(vo);
            return result;
        } catch (Exception e) {
            log.error("账户新增失败：{}", e.getMessage());
        }
        return 0;
    }

    @Override
    public int updateAccount(HttpServletRequest request, AccountUpdateVo updateVo) {
        AccountDetailInfoVo vo = new AccountDetailInfoVo();
        BeanUtils.copyProperties(updateVo, vo);
        setOperateUser(request, vo);
        try {
            int result = accountMapper.updateAccountInfo(vo);
            return result;
        } catch (Exception e) {
            log.error("账户更新失败：{}", e.getMessage());
        }
        return 0;
    }

    //最后操作用户
    private void setOperateUser(HttpServletRequest request, AccountDetailInfoVo vo) {
        String authorization = request.getHeader("Authorization");
        String[] authorizationStr = authorization.split("-");
        if (authorizationStr.length > 1) {
            vo.setLast_operate_admin(authorizationStr[1]);
        }
    }

}
