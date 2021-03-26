package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 推送结果记录
 * @Author shixh
 * @Date 2019/9/10
 **/
@Entity(name="push_article_send_result")
@Data
public class PushArticleSendResult extends BaseModel{

    private Long pushArticleId;
    private String pushChannel;//参照枚举PushChannel

    private long sendNum;
    private long getNum;
    private long clickNum;
    private long clickAppStart;
    private long clickOpenDetail;

    private String businessId;//推送成功返回的业务ID，用来查询推送状态等信息，比如VIVIO返回task_id的
    private String appPackage;//马甲包，对应数据参照魔方后台的channel_group表

}
