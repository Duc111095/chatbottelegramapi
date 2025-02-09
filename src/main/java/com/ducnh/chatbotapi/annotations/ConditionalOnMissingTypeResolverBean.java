package com.ducnh.chatbotapi.annotations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnMissingBean(parameterizedContainer = TypeResolved.class)
public @interface ConditionalOnMissingTypeResolverBean {
    @AliasFor(annotation = ConditionalOnMissingBean.class)
    Class<?>[] value() default {};
}
