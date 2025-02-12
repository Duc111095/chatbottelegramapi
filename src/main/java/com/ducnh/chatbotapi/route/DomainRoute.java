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
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ducnh.chatbotapi.constant.TelegramTextStyled.BOLD;

@BotRoute
@RequiredArgsConstructor
public class DomainRoute {
    private final GeneralPriceService generalPriceService;
    @CommandDescription("Lấy thông tin bảng giá chung")
    @CommandMapping(value = "/bgc", parseMode = MessageParseMode.HTML, allowAllUserAccess = true)
    public Mono<String> getGeneralPriceInfo(Update update, @CommandBody(description = "Thông tin cần lấy dữ liệu") String commandBody) {
        MessageParser messageParser = new MessageParser(update.getMessage().getText());
        String info = messageParser.getRemainingText();
        if (info != null && !info.isBlank()) {
            try {
                String[] infos = info.split(" ");
                String unit = infos[0];
                if (infos.length > 1) {
                    String refs = info.split(unit + " ")[1];
                    StringBuilder sb = new StringBuilder();
                    String title = TelegramMessageUtils.wrapByTag(CommonConstant.GENERAL_INFO_TITLE, TelegramTextStyled.BOLD);
                    List<GeneralPriceInfo> infoList = generalPriceService.getGeneralMessage(unit, refs, 0);
                    AtomicInteger atomicInteger = new AtomicInteger(1);
                    List<String> body = infoList.stream()
                            .map(GeneralPriceInfo::toString)
                            .sorted()
                            .map(s -> atomicInteger.getAndIncrement() + ". " + s)
                            .toList();
                    sb.append(title);
                    sb.append(System.lineSeparator());
                    body.forEach(e -> {
                        sb.append(e);
                        sb.append(System.lineSeparator());
                    });
                    return Mono.just(sb.toString());
                }
                else return Mono.just("Vui lòng nhập đúng định dạng");
            }
            catch (Exception ex) {
                return Mono.just(ex.getMessage());
            }
        } else {
            return Mono.just("Vui lòng nhập đúng định dạng");
        }
    }
}
