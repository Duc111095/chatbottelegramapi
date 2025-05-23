package com.ducnh.chatbotapi.controller;

import com.ducnh.chatbotapi.core.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.updatesreceivers.ServerlessWebhook;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "ducnh.bot.webhook.use-webhook", havingValue = "true")
@RequestMapping("/callback")
public class CallbackController {
    private final ServerlessWebhook webhook;
    private final BotProperties botProperties;

    @PostMapping("/{botPath}")
    public ResponseEntity<CallbackResponse> updateReceived(@RequestHeader("X-Telegram-Bot-Api-Secret-Token") String token, @PathVariable("botPath") String botPath, @RequestBody Update update) {
        CallbackResponse response;
        if (StringUtils.equals(botProperties.getWebhook().getSecretToken(), token)) {
            try {
                webhook.updateReceived(botPath, update);
                response = new CallbackResponse(true, HttpStatus.OK.toString());
            } catch (NoSuchElementException e) {
                response = new CallbackResponse(false, HttpStatus.NOT_FOUND.toString());
            } catch (TelegramApiValidationException e) {
                response = new CallbackResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.toString());
            }
        } else {
            response = new CallbackResponse(false, HttpStatus.UNAUTHORIZED.toString());
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/{botPath}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testReceived(@PathVariable("botPath") String botPath) {
        if (StringUtils.equals(botProperties.getUsername(), botPath)) {
            return ResponseEntity.ok(String.format("Hi there %s!", botPath));
        } else {
            return ResponseEntity.ok(String.format("Callback not found for %s", botPath));
        }
    }

    public static class CallbackResponse {
        private final boolean success;
        private final String message;

        public CallbackResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
