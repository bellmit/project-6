package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseUserBuryingPoint;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;


@Data
@ApiModel("西柚埋点表")
@Entity(name="xy_burying_point")
public class UserBuryingPoint extends BaseUserBuryingPoint {

    public static final int NEW = 10;//10-新用户
    public static final int OLD = 20;//20老用户
}