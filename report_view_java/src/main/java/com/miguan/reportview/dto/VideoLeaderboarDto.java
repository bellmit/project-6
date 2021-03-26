package com.miguan.reportview.dto;

import lombok.Data;

@Data
public class VideoLeaderboarDto {

    private Long video_id;
    private Long number_of_views;

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
