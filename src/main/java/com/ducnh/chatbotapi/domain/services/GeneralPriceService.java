package com.ducnh.chatbotapi.domain.services;

import com.ducnh.chatbotapi.domain.model.GeneralPriceInfo;

import java.util.List;
import java.util.Optional;

public interface GeneralPriceService {
    List<GeneralPriceInfo> getGeneralMessage(String unit, String refs, Integer typeReport);
}
