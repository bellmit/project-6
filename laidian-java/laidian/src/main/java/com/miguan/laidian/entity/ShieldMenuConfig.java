package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class ShieldMenuConfig implements RowMapper {
    @ApiModelProperty("菜单栏key")
    private String cks;

    @ApiModelProperty("功能是否屏蔽0否 1是")
    private int ct;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        ShieldMenuConfig shieldMenuConfig = new ShieldMenuConfig();
        shieldMenuConfig.setCks(resultSet.getString(1));
        shieldMenuConfig.setCt(resultSet.getInt(2));
        return shieldMenuConfig;
    }
}
