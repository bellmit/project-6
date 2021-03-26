package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.ClUserVo;
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

public interface ClUserMapper{

	/**
	 * 
	 * 通过条件查询用户列表
	 * 
	 **/
	List<ClUserVo>  findClUserList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户信息
	 * 
	 **/
	int saveClUser(ClUserVo clUserVo);

	/**
	 * 
	 * 修改用户信息
	 * 
	 **/
	int updateClUser(ClUserVo clUserVo);

	/**
	 * 获取全部 Huawei 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select huawei_token from cl_user where state = '10' and huawei_token is not null and huawei_token!='' and app_package = #{appPackage} ")
	List<String> findAllHuaweiToken(@Param("appPackage") String appPackage);

	/**
	 * 获取全部 Xiaomi 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select xiaomi_token from cl_user where state = '10' and xiaomi_token is not null and xiaomi_token!='' and app_package = #{appPackage} ")
	List<String> findAllXiaoMiToken(@Param("appPackage") String appPackage);

	/**
	 * 获取全部 Oppo 的tokens,mapper方法，传入的是 appPackage 字符串
	 * @param appPackage
	 * @return
	 */
	@Select("select oppo_token from cl_user where state = '10' and oppo_token is not null and oppo_token!='' and app_package = #{appPackage} ")
	List<String> findAllOppoToken(@Param("appPackage") String appPackage);

	/**
	 * 查询所有用户的token信息
	 * @return
	 */
	@Cacheable(value = CacheConstant.FIND_All_Tokens, unless = "#result == null || #result.size()==0")
	List<ClUserVo> findAllTokens(Map<String, Object> params);

	/**
	 * 根据传入的 id 查找用户
	 */
	@Select("select * from cl_user where id = #{id} ")
	ClUserVo findClUserById(@Param("id") String id);

	/**
	 * 根据用户ids查询用户信息
	 * @param ids
	 * @return
	 */
	List<ClUserVo> findUserListByIds(@Param("ids") List ids);

	void deleteDeviceId();

	/**
	 * 根据传入的 id 查找用户
	 */
	@Select("select * from cl_user where device_id = #{deviceId} ")
	List<ClUserVo> findClUserByDeviceId(@Param("deviceId") String deviceId);
}