package com.miguan.report.mapper;

import com.miguan.report.entity.BannerData;
import com.miguan.report.entity.BannerDataTotalName;
import com.miguan.report.entity.BannerDataUserBehavior;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**导入相关接口
 * @author zhongli
 * @date 2020-06-30 
 *
 */
public interface ImportMapper {

    int deleteBannerDataByDateAndPlatForm(@Param("date") String date, @Param("platForm") int platForm);

    int deleteBannerDataTotalNameByDateAndPlatForm(@Param("date") String date, @Param("platForm") int platForm);

    int deleteBannerDataUserBehaviorByDate(@Param("date") String date);

    int addBannerData(@Param("datas") List<BannerData> datas);

    int addBannerDataTotalName(@Param("datas") List<BannerDataTotalName> datas);

    int addUserBehavior(@Param("datas") List<BannerDataUserBehavior> datas);
}
