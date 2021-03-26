package com.miguan.ballvideo.entity2.advert;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**广告配置
 * @Author shixh
 * @Date 2020/4/24
 **/
@Data
@Entity(name = "ad_advert_config_code")
public class AdvertConfigCode extends BaseModel{

    private Long configId;//配置ID

    private Long codeId;//代码位ID

    private int optionValue;//排序值或者概率值

}
