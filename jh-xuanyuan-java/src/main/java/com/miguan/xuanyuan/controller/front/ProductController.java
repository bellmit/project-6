package com.miguan.xuanyuan.controller.front;


import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.request.ProductAddRequest;
import com.miguan.xuanyuan.dto.request.ProductEditRequest;
import com.miguan.xuanyuan.entity.XyProduct;
import com.miguan.xuanyuan.service.XyProductService;
import com.miguan.xuanyuan.vo.ProductVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author kangxuening
 * @since 2021-03-02
 */
@Api(value = "产品管理", tags = {"产品管理"})
@RestController
@RequestMapping("/api/front/product")
public class ProductController extends AuthBaseController {

    @Resource
    XyProductService productService;

    @ApiOperation("添加产品")
    @PostMapping(value = {"/addProduct"})
    @LogInfo(pathName="前台-产品管理-添加产品", plat= XyConstant.FRONT_PLAT, type = XyConstant.LOG_ADD)
    public ResultMap add(@RequestBody @Valid ProductAddRequest productAddRequest) {

        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        try {
            productAddRequest.check();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        }

        String productName = productAddRequest.getProductName();
        XyProduct productInfo = productService.getProductByNameExcludeId(productName, null);
        if (productInfo != null) {
            return ResultMap.error("名称不能重复");
        }

        XyProduct newProduct = null;
        try {
            newProduct = productService.addProduct(userId, productName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }

        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(newProduct, vo);
        return ResultMap.success(vo);
    }

    @ApiOperation("编辑产品")
    @PostMapping(value = {"/editProduct"})
    @LogInfo(pathName="前台-产品管理-编辑产品", plat= XyConstant.FRONT_PLAT, type = XyConstant.LOG_ADD)
    public ResultMap edit(@RequestBody @Valid ProductEditRequest productEditRequest) {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        try {
            productEditRequest.check();
        } catch (ValidateException e) {
            return ResultMap.error(e.getMessage());
        }

        XyProduct productInfo = productService.getById(productEditRequest.getId());
        if (productInfo == null) {
            return ResultMap.error("id参数错误");
        }

        String productName = productEditRequest.getProductName();
        Long productId = productEditRequest.getId();
        XyProduct otherProduct = productService.getProductByNameExcludeId(productName, productId);
        if (otherProduct != null) {
            return ResultMap.error("名称不能重复");
        }

        productInfo.setProductName(productEditRequest.getProductName());

        try {
            productService.updateById(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }
        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(productInfo, vo);
        return ResultMap.success(vo);
    }

    @ApiOperation("编辑产品")
    @GetMapping(value = {"/getList"})
    public ResultMap getProductList(@RequestParam("pageSize")Integer pageSize, @RequestParam("pageNum")Integer pageNum) {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();

        pageSize = pageSize == null ? XyConstant.INIT_PAGE_SIZE : pageSize;
        pageNum = pageNum == null ? XyConstant.INIT_PAGE : pageNum;
        pageSize = Math.min(Math.max(0, pageSize), XyConstant.INIT_MAX_PAGE_SIZE);
        pageNum = Math.max(1, pageNum);

        int offset = (pageNum - 1) * pageSize;


        List<ProductVo> list = productService.getProductList(userId, offset, pageSize);
        Integer total = productService.getProductCount(userId);

        Map<String, Object> data = new HashMap<>();

        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("total", total);
        data.put("list", list);

        return ResultMap.success(data);

    }

    @ApiOperation("查询所有的产品")
    @GetMapping("/list")
    public ResultMap<List<XyProduct>> list() {
        JwtUser userInfo = getCurrentUser();
        if (userInfo == null) {
            return ResultMap.error("用户未登录");
        }
        Long userId = userInfo.getUserId();
        return ResultMap.success(productService.getlist(userId));
    }
}
