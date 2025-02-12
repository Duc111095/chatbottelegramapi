package com.ducnh.chatbotapi.domain.services;

import com.ducnh.chatbotapi.domain.mapper.MessageMapper;
import com.ducnh.chatbotapi.domain.model.GeneralPriceInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.List;
import java.util.Map;

public class GeneralPriceServiceImpl implements GeneralPriceService {
    private final JdbcTemplate jdbcTemplate;
    private final MessageMapper mapper;
    public GeneralPriceServiceImpl(JdbcTemplate jdbcTemplate, MessageMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<GeneralPriceInfo> getGeneralMessage(String unit, String refs, Integer typeReport) {
        SimpleJdbcCall getGeneralPriceInfo = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("check_bgc")
                .returningResultSet("result", mapper);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("ma_bp", unit)
                .addValue("list_vt", refs)
                .addValue("error_yn", typeReport);

        Map<String, Object> resultMap = getGeneralPriceInfo.execute(sqlParameterSource);
        return (List<GeneralPriceInfo>) resultMap.get("result");
    }
}
