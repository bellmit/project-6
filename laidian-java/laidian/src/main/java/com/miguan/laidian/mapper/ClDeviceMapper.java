package com.miguan.laidian.mapper;

import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.ClUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 用户表Mapper
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClDeviceMapper {


	/**
	 * 
	 * 通过条件查询设备
	 * @param params
	 * @return
	 * 
	 **/
	ClDeviceVo getDeviceByDeviceIdAppType(Map<String, Object> params);

	/**
	 * 
	 * 新增用户信息
	 * @param clDeviceVo
	 * @return
	 * 
	 **/
	int saveClDevice(ClDeviceVo clDeviceVo);

	/**
	 * 
	 * 修改用户信息
	 * @param clDeviceVo
	 * @return
	 * 
	 **/
	int updateClDevice(ClDeviceVo clDeviceVo);

	/**
	 * 获取全部 Huawei 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select huawei_token from cl_device where state = '10' and huawei_token is not null and huawei_token!='' and app_package = #{appPackage} ")
	List<String> findAllHuaweiToken(@Param("appPackage") String appPackage);

	/**
	 * 获取全部 Xiaomi 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select xiaomi_token from cl_device where state = '10' and xiaomi_token is not null and xiaomi_token!='' and app_package = #{appPackage} ")
	List<String> findAllXiaoMiToken(@Param("appPackage") String appPackage);

	/**
	 * 获取全部 Oppo 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select oppo_token from cl_device where state = '10' and oppo_token is not null and oppo_token!='' and app_package = #{appPackage} ")
	List<String> findAllOppoToken(@Param("appPackage") String appPackage);

	/**
	 * 查询所有用户的token信息
	 * @return
	 */
	@Cacheable(value = CacheConstant.FIND_All_Device_Tokens, unless = "#result == null || #result.size()==0")
	List<ClUserVo> findAllTokens(Map<String, Object> params);

	/**
	 *  统计所有用户的token信息条数
	 * @param params
	 * @return
	 */
	int  countClDevice(Map<String, Object> params);

	void updateDistinctId(Map<String, Object> params);

	List<ClDeviceVo> findAllDeviceId(int index);

	List<ClDeviceVo> findAllTokensByDistinct(Map<String,Object> map);

	long countAllDeviceId();
}