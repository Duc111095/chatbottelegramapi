package com.ducnh.chatbotapi.route;

import com.ducnh.chatbotapi.annotations.BotRoute;
import com.ducnh.chatbotapi.annotations.CommandBody;
import com.ducnh.chatbotapi.annotations.CommandDescription;
import com.ducnh.chatbotapi.annotations.CommandMapping;
import com.ducnh.chatbotapi.constant.CommonConstant;
import com.ducnh.chatbotapi.constant.MessageParseMode;
import com.ducnh.chatbotapi.constant.TelegramTextStyled;
import com.ducnh.chatbotapi.domain.model.GeneralPriceInfo;
import com.ducnh.chatbotapi.domain.services.GeneralPriceService;
import com.ducnh.chatbotapi.model.MessageParser;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@BotRoute
@RequiredArgsConstructor
@Slf4j
public class DomainRoute {
    private final GeneralPriceService generalPriceService;
    @CommandDescription("Lấy thông tin bảng giá chung")
    @CommandMapping(value = "/bgc", parseMode = MessageParseMode.HTML, allowAllUserAccess = true)
    public Mono<String> getGeneralPriceInfo(Update update, @CommandBody(description = "Thông tin cần lấy dữ liệu") String commandBody) {
        MessageParser messageParser = new MessageParser(update.getMessage().getText());
        String info = messageParser.getRemainingText();
        if (info != null && !info.isEmpty()) {
            try {
                String[] infos = info.split(" ");
                String units = infos[0];
                System.out.println(units);
                System.out.println(Arrays.toString(infos));

                if (infos.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    Arrays.stream(units.split(",")).forEach(
                            unit -> {
                                System.out.println(unit);
                                String typeReport = infos[1];
                                String refs = info.split(units + " " + typeReport + " ")[1];
                                String title = TelegramMessageUtils.wrapByTag(CommonConstant.GENERAL_INFO_TITLE + " " + unit.toUpperCase(), TelegramTextStyled.BOLD);
                                List<GeneralPriceInfo> infoList = generalPriceService.getGeneralMessage(unit, refs, Integer.valueOf(typeReport));
                                AtomicInteger atomicInteger = new AtomicInteger(1);
                                List<String> body = infoList.stream()
                                        .map(GeneralPriceInfo::toString)
                                        .sorted()
                                        .map(s -> atomicInteger.getAndIncrement() + ". " + s)
                                        .collect(Collectors.toList());
                                sb.append(title);
                                sb.append(System.lineSeparator());
                                body.forEach(e -> {
                                    sb.append(e);
                                    sb.append(System.lineSeparator());
                                });
                            });
                    return Mono.just(sb.toString());
                } else {
                    return Mono.just(CommonConstant.GENERAL_INFO_ERROR);
                }
            }
            catch (Exception ex) {
                log.error("Error when processing Bot BGC: " + ex.getMessage());
                return Mono.just(CommonConstant.GENERAL_INFO_ERROR);
            }
        }
        return Mono.just(CommonConstant.GENERAL_INFO_ERROR);
    }
}
