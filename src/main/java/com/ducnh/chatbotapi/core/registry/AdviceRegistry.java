package com.ducnh.chatbotapi.core.registry;

import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class AdviceRegistry {
    private final Map<Class<? extends Throwable>, Advice> adviceMap = new ConcurrentHashMap<>();
    public void register(Class<? extends Throwable>[] classes, Method method, Object bean) {
        for (Class<? extends Throwable> clazz : classes) {
            adviceMap.put(clazz, new Advice(bean, method));
        }
    }

    public boolean hasAdvice(Class<? extends Throwable> clazz) {
        return adviceMap.containsKey(clazz);
    }

    public Advice getAdvice(Class<? extends Throwable> clazz) {
        return adviceMap.get(clazz);
    }

    public static class Advice {
        private final Object bean;
        private final Method method;

        public Advice(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        public Object getBean() {
            return bean;
        }

        public Method getMethod() {
            return method;
        }
    }
}
