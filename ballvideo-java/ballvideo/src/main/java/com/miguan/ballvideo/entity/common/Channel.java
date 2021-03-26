package com.miguan.ballvideo.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel implements Serializable {

    public static final String CHANNEL_REDIS = "channelRedis";

    private String channelId;

    private String domain;

}
