package com.miguan.xuanyuan.service.impl;

import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.DateUtils;
import com.miguan.xuanyuan.dto.XyStrategyCodeDto;
import com.miguan.xuanyuan.dto.XyStrategyDto;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyStrategy;
import com.miguan.xuanyuan.entity.XyStrategyCode;
import com.miguan.xuanyuan.mapper.XyStrategyCodeMapper;
import com.miguan.xuanyuan.mapper.XyStrategyMapper;
import com.miguan.xuanyuan.service.XyAdCodeService;
import com.miguan.xuanyuan.service.XyStrategyCodeService;
import com.miguan.xuanyuan.service.XyStrategyService;
import com.miguan.xuanyuan.vo.AdCodeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class XyStrategyCodeServiceImpl implements XyStrategyCodeService {

    @Resource
    public XyStrategyCodeMapper strategyCodeMapper;

    @Resource
    XyAdCodeService xyAdCodeService;

    public int insert(XyStrategyCode xyStrategyCode){
        int id = strategyCodeMapper.insert(xyStrategyCode);
        return id;
    }

    public int update(XyStrategyCode xyStrategyCode) {
        return strategyCodeMapper.update(xyStrategyCode);
    }

    /**
     * 根据策略id获取策略代码位列表
     *
     * @param strategyId
     * @return
     */
    public List<XyStrategyCodeDto> getStrategyCodeList(long strategyId) {
        return strategyCodeMapper.getStrategyCodeList(strategyId);
    }
    /**
     * 获取所有代码位列表
     *
     * @param positionId
     * @return
     */
    public List<XyStrategyCodeDto> getAllStrategyCodeList(Long strategyGroupId, Long positionId) {
        return strategyCodeMapper.getAllStrategyCodeList(strategyGroupId, positionId);
    }

    /**
     * 获取广告源数据
     *
     * @param adCodeId
     * @return
     */
    public XyStrategyCodeDto getStrategyCodeInfo(Long adCodeId) {
        return strategyCodeMapper.getStrategyCodeInfo(adCodeId);
    }


    /**
     * 根据广告代码位获取数据
     * @param adCodeId
     * @return
     */
    public XyStrategyCode getStrategyCodeListByAdCodeId(Long strategyId, Long adCodeId) {
        return strategyCodeMapper.getStrategyCodeListByAdCodeId(strategyId, adCodeId);
    }

    /**
     * 关闭所有代码位
     *
     * @param strategyId
     * @return
     */
    public int updateStrategyCodeClose(Long strategyId) {
        return strategyCodeMapper.updateStrategyCodeClose(strategyId);
    }

    /**
     * 设置数据
     *
     * @param xyStrategyCode
     */
    public void putStrategyCode(XyStrategyCode xyStrategyCode) throws ServiceException {
        Long adCodeId = xyStrategyCode.getAdCodeId();
        if (adCodeId == null) {
            throw new ServiceException("adCodeId不能为空");
        }
        XyStrategyCode result = getStrategyCodeListByAdCodeId(xyStrategyCode.getStrategyId(), xyStrategyCode.getAdCodeId());
        if (result != null) {
            xyStrategyCode.setId(result.getId());
            update(xyStrategyCode);
        } else {
            AdCodeVo xyAdCode = xyAdCodeService.findById(xyStrategyCode.getAdCodeId());
            xyStrategyCode.setAdCodeId(xyAdCode.getId());
            xyStrategyCode.setCodeId(xyAdCode.getSourceCodeId());
            insert(xyStrategyCode);
        }
    }

}
