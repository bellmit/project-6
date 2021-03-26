package com.miguan.xuanyuan.dto.ab;

import lombok.Data;

@Data
public class AbTraffic {
    private Integer id;
    private String name;
    private Integer traffic;

    public AbTraffic() {
    }

    public AbTraffic(Integer id, String name, Integer traffic) {
        this.id = id;
        this.name = name;
        this.traffic = traffic;
    }
}
