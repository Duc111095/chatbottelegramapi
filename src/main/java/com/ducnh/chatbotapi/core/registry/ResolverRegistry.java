package com.ducnh.chatbotapi.core.registry;

import com.ducnh.chatbotapi.core.resolver.TypeResolver;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor
public class ResolverRegistry {
    private final Map<Class<Object>, TypeResolver<Object>> resolverMap = new ConcurrentHashMap<>();
    public TypeResolver<Object> getResolverByType(Class<Object> clazz) {
        return resolverMap.get(clazz);
    }
    public void register(TypeResolver<Object> resolver) {
        resolverMap.putIfAbsent(resolver.getType(), resolver);
    }

    public Set<Class<Object>> getSupportedTypes() {
        return resolverMap.keySet();
    }
}
