package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseUserBuryingPoint;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;

@Data
@ApiModel("西柚埋点表(天)")
@Entity(name="xy_burying_point_day")
public class UserBuryingPointDay extends BaseUserBuryingPoint {

}