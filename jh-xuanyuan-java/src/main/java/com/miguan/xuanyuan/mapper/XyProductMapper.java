package com.miguan.xuanyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.xuanyuan.entity.XyProduct;
import com.miguan.xuanyuan.vo.ProductVo;

import java.util.List;

/**
 * <p>
 * 产品表 Mapper 接口
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-23
 */
public interface XyProductMapper extends BaseMapper<XyProduct> {
    XyProduct getProductByNameExcludeId(String productName, Long id);

    List<ProductVo> getProductList(Long userId, Integer offset, Integer limit);

    Integer getProductCount(Long userId);
}
