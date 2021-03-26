package com.miguan.xuanyuan.mapper;

import com.github.pagehelper.Page;
import com.miguan.xuanyuan.entity.XyApp;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.vo.XyAppDetailVo;
import com.miguan.xuanyuan.vo.XyAppSimpleVo;
import com.miguan.xuanyuan.vo.XyAppVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 应用
 * @Date 2021/1/21
 **/
public interface XyAppMapper extends BaseMapper<XyApp> {
    Page<XyAppVo> findPageList(Map params);
    int deleteById(@Param("id") Long id);
    int judgeExistPackage(@Param("packageName") String packageName, @Param("id") Long id);
    void updateStatus(@Param("id")  Long appId,@Param("status")  Integer status);
    XyApp findStatus(@Param("id")  Long appId,@Param("status")  Integer status);

    XyAppDetailVo findDetailById(Long id);

    List<XyAppSimpleVo> findList(@Param("plat")int plat, @Param("userId") Long userId);

    int existAppInfo(@Param("appKey") String appKey, @Param("secretKey") String secretKey,@Param("sha") String SHA1, @Param("status") int status);

    XyApp findByAppKeyAndSecret(@Param("appKey") String appKey,@Param("secretKey") String secretKey,@Param("status") int status);
}
