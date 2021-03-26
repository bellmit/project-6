package com.miguan.ballvideo.entity2.advert;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**广告的代码位
 * @Author shixh
 * @Date 2020/4/24
 **/
@Data
@Entity(name = "ad_advert_code")
public class AdvertCode extends BaseModel{

    private String adId;//代码位ID：第三方或98广告后台生成的广告ID

    private String platKey;//广告平台，对应表ad_plat

    private String typeKey;//广告类型,对应表ad_type

    private String renderKey;//渲染类型，对应表ad_randerer

    private String materialKey;//广告素材，对应表ad_meterial

    private String advCss;//广告样式

    private String sdkKey;//使用SDK，对应表ad_sdk

    private String appPackage;//应用包

    private String version1;//版本区间1

    private String version2;//版本区间2

    private int channelType;//渠道过滤类型 1-全部，2-包含，3-不包含

    private String channelIds;//多个逗号隔开

    private String permission ;//是否需要权限

    private int state;//0-关闭，1开启

}
