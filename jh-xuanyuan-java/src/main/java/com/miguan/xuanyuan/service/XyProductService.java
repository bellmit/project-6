package com.miguan.xuanyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.xuanyuan.entity.XyProduct;
import com.miguan.xuanyuan.vo.ProductVo;
import com.sun.org.apache.xpath.internal.objects.XString;

import java.util.List;

/**
 * <p>
 * 产品表 服务类
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-23
 */
public interface XyProductService extends IService<XyProduct> {

    XyProduct getProductByNameExcludeId(String productName, Long id);

    XyProduct addProduct(Long userId, String productName);

    List<ProductVo> getProductList(Long userId, Integer offset, Integer limit);

    Integer getProductCount(Long userId);

    List<XyProduct> getlist(Long userId);
}
