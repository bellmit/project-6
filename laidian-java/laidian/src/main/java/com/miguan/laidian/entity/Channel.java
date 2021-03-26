package com.miguan.laidian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel implements RowMapper {

    public static final String CHANNEL_REDIS = "channelRedis";

    private String channelId;

    private String domain;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        Channel channel = new Channel();
        channel.setChannelId(resultSet.getString(1));
        channel.setDomain(resultSet.getString(2));
        return channel;
    }
}
