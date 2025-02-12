package com.ducnh.chatbotapi.domain.mapper;

import com.ducnh.chatbotapi.domain.model.GeneralPriceInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MessageMapper implements RowMapper<GeneralPriceInfo> {
    @Override
    public GeneralPriceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneralPriceInfo info =  new GeneralPriceInfo();
        info.setRefNo(rs.getString("ma_vt"));
        info.setTypeMessage(rs.getInt("loai_msg"));
        info.setMessage(rs.getString("message"));
        return info;
    }
}
