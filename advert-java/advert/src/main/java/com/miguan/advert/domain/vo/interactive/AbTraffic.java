package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

@Data
public class AbTraffic {
    private Integer id;
    private String name;
    private Integer traffic;

    public AbTraffic() {
    }

    public AbTraffic(Integer id,String name, Integer traffic) {
        this.id = id;
        this.name = name;
        this.traffic = traffic;
    }
}
