package com.miguan.bigdata.dto;

import lombok.Data;

@Data
public class VideoLeaderboarDto {

    private Integer video_id;
    private Integer number_of_views;

    @Override
    public boolean equals(Object o) {
        if (o instanceof VideoLeaderboarDto) {
            VideoLeaderboarDto dto = (VideoLeaderboarDto) o;
            if (video_id.longValue() == dto.getVideo_id().longValue()) {
                return true;
            }
        }
        return false;
    }

}
