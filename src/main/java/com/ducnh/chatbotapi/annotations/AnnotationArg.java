package com.ducnh.chatbotapi.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationArg {
    Class<? extends Annotation> value();
}
