package com.miguan.xuanyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.entity.XyProduct;
import com.miguan.xuanyuan.mapper.XyProductMapper;
import com.miguan.xuanyuan.service.XyProductService;
import com.miguan.xuanyuan.vo.ProductVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品表 服务实现类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-23
 */
@Service
public class XyProductServiceImpl extends ServiceImpl<XyProductMapper, XyProduct> implements XyProductService {


    /**
     * 根据名称获取除了给定id以外的产品，判断是否名称重复
     *
     * @param productName
     * @param id
     * @return
     */
    public XyProduct getProductByNameExcludeId(String productName, Long id) {
        XyProductMapper mapper = this.getBaseMapper();
        return mapper.getProductByNameExcludeId(productName, id);
    }

    /**
     * 添加产品
     *
     * @param userId
     * @param productName
     * @return
     */
    public XyProduct addProduct(Long userId, String productName) {
        XyProduct product = new XyProduct();
        product.setUserId(userId);
        product.setProductName(productName);
        product.setStatus(XyConstant.STATUS_NORMAL);
        this.save(product);
        return product;
    }

    public List<ProductVo> getProductList(Long userId, Integer offset, Integer limit) {
        XyProductMapper mapper = this.getBaseMapper();
        return mapper.getProductList(userId, offset, limit);
    }

    public Integer getProductCount(Long userId) {
        XyProductMapper mapper = this.getBaseMapper();
        return mapper.getProductCount(userId);
    }

    @Override
    public List<XyProduct> getlist(Long userId) {
        QueryWrapper<XyProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }
}
