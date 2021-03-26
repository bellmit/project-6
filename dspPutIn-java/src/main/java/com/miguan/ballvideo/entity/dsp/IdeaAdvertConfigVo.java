package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**配置表
 * @Author suhj
 * @Date 2020/8/20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAdvertConfigVo {
    //ID
    private String id;
    //编码
    private String code;
    //名称
    private String name;
    //值
    private String value;
    //备注
    private String remark;
    //状态
    private String state;

    private Date createdAt;

    private Date updatedAt;
}
