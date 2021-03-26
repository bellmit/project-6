package com.miguan.laidian.entity;

import com.miguan.laidian.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 推送结果记录
 * @Author laiyd
 * @Date 2020/4/14
 **/
@Entity(name="push_article_send_result")
@Data
public class PushArticleSendResult extends BaseModel {

    private Long pushArticleId;
    private String pushChannel;//参照枚举PushChannel

    private long sendNum;
    private long getNum;
    private long clickNum;
    private long clickAppStart;
    private long clickOpenDetail;

    private String businessId;//推送成功返回的业务ID，用来查询推送状态等信息，比如VIVIO返回task_id的
    private String appType;//app类型 微来电-wld,炫来电-xld

}
