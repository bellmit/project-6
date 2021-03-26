package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * jpa entity class for dw_app_version_channel_dict
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class DwAppVersionChannelDict  implements Serializable {

	private String packageName;

	private String appVersion;

	private String channel;

	private String fatherChannel;

}
