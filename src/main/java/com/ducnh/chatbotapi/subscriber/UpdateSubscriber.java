package com.ducnh.chatbotapi.subscriber;

import com.ducnh.chatbotapi.annotations.AnnotationArg;
import com.ducnh.chatbotapi.annotations.TypeArg;
import com.ducnh.chatbotapi.constant.CommonConstant;
import com.ducnh.chatbotapi.core.BotDispatcher;
import com.ducnh.chatbotapi.core.BotProperties;
import com.ducnh.chatbotapi.core.registry.AdviceRegistry;
import com.ducnh.chatbotapi.core.registry.ResolverRegistry;
import com.ducnh.chatbotapi.exception.BotAccessDeniedException;
import com.ducnh.chatbotapi.mapper.UpdateMapper;
import com.ducnh.chatbotapi.model.BotCommand;
import com.ducnh.chatbotapi.model.BotCommandParams;
import com.ducnh.chatbotapi.model.UpdateTrace;
import com.ducnh.chatbotapi.repository.UpdateTraceRepository;
import com.ducnh.chatbotapi.utils.ReflectUtils;
import com.ducnh.chatbotapi.utils.TelegramMessageUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class UpdateSubscriber implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BotProperties getBotProperties() {
        return applicationContext.getBean(BotProperties.class);
    };

    private UpdateMapper getUpdateMapper() {
        return applicationContext.getBean("updateMapper",UpdateMapper.class);
    }

    private UpdateTraceRepository getUpdateTraceRepository() {
        return applicationContext.getBean(UpdateTraceRepository.class);
    }

    private Executor getTaskExecutor() {
        return applicationContext.getBean("botAsyncTaskExecutor", SimpleAsyncTaskExecutor.class);
    }

    private ResolverRegistry getResolverRegistry() {
        return applicationContext.getBean(ResolverRegistry.class);
    }

    private boolean isUpdateTraceEnabled() {
        BotProperties botProperties = getBotProperties();
        return botProperties.getEnableUpdateTrace() != null && botProperties.getEnableUpdateTrace();
    }

    private <T> OptionalInt getIndexArgByType(Parameter[] parameters, Class<T> type) {
        return IntStream.range(0, parameters.length)
                .filter(i -> parameters[i].getType() == type)
                .findFirst();
    }

    private <T extends Annotation> OptionalInt getIndexArgByAnnotation(Parameter[] parameters, Class<T> annotationType) {
        return IntStream.range(0, parameters.length)
                .filter(i -> AnnotationUtils.findAnnotation(parameters[i], annotationType) != null)
                .findFirst();
    }

    @SneakyThrows
    private Object[] getBotCommandArgs(Method method, BotCommandParams botCommandParams) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        FieldUtils.getAllFieldsList(BotCommandParams.class)
                .forEach(field -> {
                    TypeArg typeArg = AnnotationUtils.findAnnotation(field, TypeArg.class);
                    if (typeArg != null) {
                        Class<?> fieldType = field.getType();
                        OptionalInt idx = getIndexArgByType(parameters, fieldType);
                        if (idx.isPresent()) {
                            args[idx.getAsInt()] = ReflectUtils.getProperty(botCommandParams, field.getName());
                        }
                    }
                    AnnotationArg annotationArg = AnnotationUtils.findAnnotation(field, AnnotationArg.class);
                    if (annotationArg != null) {
                        OptionalInt idx = getIndexArgByAnnotation(parameters, annotationArg.getClass());
                        if (idx.isPresent()) {
                            args[idx.getAsInt()] = ReflectUtils.getProperty(botCommandParams, field.getName());
                        }
                    }
                });
        return args;
    }

    private void logUpdate(Update update) {
        UpdateMapper updateMapper = getUpdateMapper();
        BotProperties botProperties = getBotProperties();
        log.debug("New update detected -> {}", getUpdateMapper().writeValueAsString(update));
        if (StringUtils.isNotBlank(botProperties.getLoggingChatId())) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("New update detected -> \n" +updateMapper.writeValueAsPrettyString(update));
            sendMessage.setChatId(botProperties.getLoggingChatId());
            BotDispatcher.getInstance().executeSneakyThrows(sendMessage);
        }
    }

    private void handleConsumerError(Throwable t, BotCommandParams botCommandParams) {
        if (t instanceof InvocationTargetException itex) {
            executeCommandAdvice(itex.getTargetException(), botCommandParams);
        }
        else {
            executeCommandAdvice(t, botCommandParams);
        }
    }

    public void handleReturnedValue(Supplier<Object> returnedSupplier, BotCommand botCommand, BotCommandParams botCommandParams ) {
        ResolverRegistry resolverRegistry = getResolverRegistry();
        Set<Class<Object>> supportedTypes = resolverRegistry.getSupportedTypes();
        Set<String> supportedTypesName = supportedTypes.stream()
                .map(Class::getName)
                .collect(Collectors.toSet());
        Mono.fromSupplier(returnedSupplier)
                .flatMapMany(rawReturnedValue -> {
                    if (Objects.isNull(rawReturnedValue)) {
                        log.info("Nothing to reply. Cause return value is null or it's type is Void");
                        return Flux.empty();
                    }
                    else if (rawReturnedValue instanceof Mono<?>) {
                        return ((Mono<?>) rawReturnedValue).flux();
                    }
                    else if (rawReturnedValue instanceof Flux<?>) {
                        return (Flux<?>) rawReturnedValue;
                    }
                    return Flux.just(rawReturnedValue);
                })
                .subscribeOn(Schedulers.fromExecutor(getTaskExecutor()))
                .subscribe(returnValue -> {
                    Class<?> type = returnValue.getClass();
                    Optional<Class<Object>> supportedType = supportedTypes.stream()
                            .filter(e -> e.isAssignableFrom(type))
                            .findFirst();
                    if (supportedType.isPresent()) {
                        resolverRegistry.getResolverByType(supportedType.get())
                                .resolve(returnValue, botCommand, botCommandParams);
                    }
                    else {
                        log.warn("Nothing to reply. Cause the return type is not supported ({}). Supported types are: {}", type.getName(), supportedTypesName);
                    }
                }, t -> handleConsumerError(t, botCommandParams));
    }

    @SneakyThrows
    private Object invokeMethod(Object bean, Method method, Object[] args) {
        return method.invoke(bean, args);
    }

    public void handleCmd(BotCommand botCommand, BotCommandParams botCommandParams) {
        Object[] args = getBotCommandArgs(botCommand.getMethod(), botCommandParams);
        Object route = applicationContext.getBean(botCommand.getMethod().getDeclaringClass());
        handleReturnedValue(() -> invokeMethod(route, botCommand.getMethod(), args), botCommand, botCommandParams);
    }

    private void sendUnknownErrorAlert(BotCommandParams params, Throwable t) {
        log.error("Error!", t);
        TelegramMessageUtils.replyMessage(BotDispatcher.getInstance().getAbsSender(), params.getUpdate().getMessage(), CommonConstant.ERROR_NOTIFY_MESSAGE, null);
    }

    private void executeCommandAdvice(Throwable t, BotCommandParams params) {
        AdviceRegistry adviceRegistry = applicationContext.getBean(AdviceRegistry.class);
        if (adviceRegistry.hasAdvice(t.getClass())) {
            Method handleMethod = adviceRegistry.getAdvice(t.getClass()).method();
            Object adviceBean = adviceRegistry.getAdvice(t.getClass()).bean();
            Parameter[] parameters = handleMethod.getParameters();
            Object[] args = new Object[parameters.length];
            for (int idx = 0; idx < parameters.length; idx++) {
                if (parameters[idx].getType() == Update.class) {
                    args[idx] = params.getUpdate();
                } else if (Throwable.class.isAssignableFrom(parameters[idx].getType())) {
                    args[idx] = t;
                }
            }
            Object returnValue = invokeMethod(adviceBean, handleMethod, args);
            if (returnValue == null) {
                log.warn("Returned value of {}#{} is null, so default error handler will be called as callback", adviceBean.getClass().getSimpleName(), handleMethod.getName());
                sendUnknownErrorAlert(params, t);
            }
            else if (returnValue instanceof String stringReturnedValue) {
                TelegramMessageUtils.replyMessage(BotDispatcher.getInstance().getAbsSender(), params.getUpdate().getMessage(), stringReturnedValue, null);
            }
            else if (returnValue instanceof BotApiMethod) {
                BotDispatcher.getInstance().executeSneakyThrows((BotApiMethod<? extends Serializable>) returnValue);
            }
            else {
                log.warn("Return value of {}#{} is not supported ({}), so default error handler will be called as a callback", adviceBean.getClass().getSimpleName(), handleMethod.getName(), returnValue.getClass().getName());
                sendUnknownErrorAlert(params, t);
            }
        } else {
            sendUnknownErrorAlert(params, t);
        }
    }

    private void executeCommand(Update update, BotCommandParams botCommandParams) {
        try {
            BotDispatcher.getInstance().getCommand(update)
                    .ifPresentOrElse(botCommand -> {
                        botCommandParams.setCommandName(botCommand.getCmd());
                        handleCmd(botCommand, botCommandParams);
                    }, () -> applicationContext.getBean(CommandNotFoundUpdateSubscriber.class).accept(update, botCommandParams.getCommandName()));
        } catch (BotAccessDeniedException ex) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(botCommandParams.getChatId())
                    .replyToMessageId(botCommandParams.getMessage().getMessageId())
                    .text(ex.getMessage())
                    .build();
            BotDispatcher.getInstance().executeSneakyThrows(sendMessage);
        }
    }

    private void process(Update update) {
        if (update.getCallbackQuery() != null) {
            applicationContext.getBean(CallbackQuerySubscriber.class).accept(update);
        } else {
            Message message = update.getMessage();
            boolean isCommand = !TelegramMessageUtils.isChannelPost(update) && message != null &&
                    (
                            (message.hasText() && StringUtils.startsWith(message.getText(), CommonConstant.CMD_PREFIX)) ||
                                    (message.hasPhoto() && StringUtils.startsWith(message.getCaption(), CommonConstant.CMD_PREFIX))
                    );
            if (isCommand) {
                BotCommandParams botCommandParams = BotDispatcher.getInstance().getCommandParameters(update);
                if (botCommandParams != null) {
                    executeCommand(update, botCommandParams);
                }
            }
            else {
                NonCommandUpdateSubscriber nonCommandUpdateSubscriber = applicationContext.getBean(NonCommandUpdateSubscriber.class);
                nonCommandUpdateSubscriber.accept(update);
            }
        }
    }

    private void consume(Update update) {
        this.logUpdate(update);
        if (isUpdateTraceEnabled()) {
            UpdateTraceRepository updateTraceRepository = getUpdateTraceRepository();
            updateTraceRepository.add(Mono.just(new UpdateTrace(update)));
        }
        applicationContext.getBean(PreSubscriber.class).accept(update);
        this.process(update);
        applicationContext.getBean(PosSubscriber.class).accept(update);
    }

    public void consume(Mono<Update> update) {
        update.subscribeOn(Schedulers.fromExecutor(getTaskExecutor()))
                .subscribe(this::consume);
    }

    public void consume(Flux<Update> update) {
        update.subscribeOn(Schedulers.fromExecutor(getTaskExecutor()))
                .subscribe(this::consume);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
