package com.ducnh.chatbotapi.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.SneakyThrows;

public class UpdateMapper extends ObjectMapper {
    public UpdateMapper() {
        super();
        super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        super.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        super.setDateFormat(new StdDateFormat());
    }

    @SneakyThrows
    public String writeValueAsPrettyString(Object value) {
        return super.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }

    @Override
    @SneakyThrows
    public String writeValueAsString(Object value) {
        return super.writeValueAsString(value);
    }
}
