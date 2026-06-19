package com.example.back.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemUrlProvider {
    // application.properties에 설정된 포트를 가져옴 (기본값 8080)
    @Value("${server.port:8081}")
    private String port;

    public String getCurrentSystemUrl() {
        try {
            // 현재 서버의 로컬 IP 주소 추출
            String ip = InetAddress.getLocalHost().getHostAddress();
            
            return "http://" + ip + ":" + port + "/api";
        } catch (UnknownHostException e) {
            // IP를 못 찾을 경우 fallback 처리
            return "http://localhost:" + port + "/api";
        }
    }
}
