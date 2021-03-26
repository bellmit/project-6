package com.miguan.ballvideo.entity2.advert;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**广告配置
 * @Author shixh
 * @Date 2020/4/24
 **/
@Data
@Entity(name = "ad_advert_config")
public class AdvertConfig extends BaseModel{

    private String appPackage;//应用包

    private Long positionId;//广告位置ID

    private int computer;//算法：1-概率补充；2-手动排序

    private int state;//0-关闭，1开启

}
