package com.ducnh.chatbotapi.domain.config;

import com.ducnh.chatbotapi.domain.mapper.MessageMapper;
import com.ducnh.chatbotapi.domain.services.GeneralPriceService;
import com.ducnh.chatbotapi.domain.services.GeneralPriceServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DomainConfig {
    private final ApplicationContext applicationContext;
    public DomainConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Bean
    MessageMapper mapper() {
        return new MessageMapper();
    }
    @Bean
    GeneralPriceService getGeneralPriceService() {
        return new GeneralPriceServiceImpl(applicationContext.getBean(JdbcTemplate.class), applicationContext.getBean(MessageMapper.class));
    }

    /*
    @Override
    public void run(String... args) throws Exception {
        String unit = "TB";
        String list_vt = "628020";
        GeneralPriceService generalPriceService = getGeneralPriceService();
        generalPriceService.getGeneralMessage(unit, list_vt, 0).forEach(System.out::println);
    }*/
}
