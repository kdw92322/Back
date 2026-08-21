package com.example.back.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmittersManager {
    // 동시성 처리를 위해 ConcurrentHashMap 사용
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void add(String uuid, SseEmitter emitter) {
        this.emitters.put(uuid, emitter);
        emitter.onCompletion(() -> this.emitters.remove(uuid));
        emitter.onTimeout(() -> this.emitters.remove(uuid));
    }

    public SseEmitter get(String uuid) {
        return this.emitters.get(uuid);
    }
}
