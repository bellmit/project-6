package com.miguan.ballvideo.entity2.advert;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**广告位置
 * @Author shixh
 * @Date 2020/4/24
 **/
@Data
@Entity(name = "ad_advert_position")
public class AdvertPosition extends BaseModel{

    private String appPackage;//所属应用

    private String mobileType;//应用端:1-ios，2：安卓

    private String name;//广告位名称

    private String positionType;//广告位ID

    private Integer firstLoadPosition;//首次加载

    private Integer secondLoadPosition;//再次加载

    private Integer maxShowNum;//展示次数限制

}
