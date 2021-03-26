package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseUserBuryingPoint;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;

@Data
@ApiModel("西柚埋点表(月份)")
@Entity(name="xy_burying_point_month")
public class UserBuryingPointMonth extends BaseUserBuryingPoint {

}