package com.example.back.aop;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.example.back.util.SecurityUtil;

@Aspect
@Component
public class AuthAspect {

    @Before("execution(* com.example.back.service..*.insert*(..)) || execution(* com.example.back.service..*.save*(..))")
    public void beforeInsert(JoinPoint joinPoint) {
        injectUserId(joinPoint, true);
    }

    @Before("execution(* com.example.back.service..*.update*(..))")
    public void beforeUpdate(JoinPoint joinPoint) {
        injectUserId(joinPoint, false);
    }

    @SuppressWarnings("unchecked")
    private void injectUserId(JoinPoint joinPoint, boolean isInsert) {
        String userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) arg;
                
                // 단일 Map 처리
                if (isInsert) {
                    map.put("createBy", userId);
                    map.put("updateBy", userId);
                } else {
                    map.put("updateBy", userId);
                }

                // Map 내부의 리스트 처리 (inserts, updates 키를 가진 경우)
                if (map.containsKey("inserts") && map.get("inserts") instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("inserts");
                    for (Map<String, Object> item : list) {
                        item.put("createBy", userId);
                        item.put("updateBy", userId);
                    }
                }

                if (map.containsKey("updates") && map.get("updates") instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("updates");
                    for (Map<String, Object> item : list) {
                        item.put("updateBy", userId);
                    }
                }
            }
        }
    }
}