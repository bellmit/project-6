package com.miguan.idmapping.vo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**用户设备关联信息
 * @author zhongli
 * @date 2020-07-21 
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserRefInfo {
    @Id
    private ObjectId id;

    private String uuid;
    private String init_distinct_id;
    /**
     * 用户类型 0=匿名用户 1=注册用户
     */
    private int user_type;

    /**
     * 类型：
     */
    private String from;

    /**
     * 创建时应用版本号
     */
    private  String init_app_version;

    /**
     * 创建初始渠道
     */
    private String init_channel;

    /**
     * 创建时的马甲包
     */
    private String init_package_name;

    /**
     * 注册时间
     */
    private String register_time;
    /**
     * 创建时间
     */
    private String create_time;
}
