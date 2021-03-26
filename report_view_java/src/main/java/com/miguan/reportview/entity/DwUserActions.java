package com.miguan.reportview.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * jpa entity class for dw_user_actions
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class DwUserActions  implements Serializable {

	private LocalDate dd;

	private Integer dh;

	private String uuid;

	private String packageName;

	private String appVersion;

	private String channel;

	private String changeChannel;

	private Integer isNew;

	private Integer isNewApp;

	private String model;

	private String distinctId;

	private LocalDateTime receiveTime;

	private LocalDateTime creatTime;

	private String country;

	private String province;

	private String city;

	private Integer register;

	private Integer homeView;

	private Integer adClick;

	private Integer validPlay;

}
