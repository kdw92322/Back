package com.example.back;

import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.PostConstruct;

@EnableAsync
@SpringBootApplication
public class BackApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	    return builder.sources(BackApplication.class);
	}
    
	@Bean(name = "sqlSession")
	SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws Exception {
	    return new SqlSessionTemplate(sqlSessionFactory);
	}

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> configuration.setLogImpl(Log4j2Impl.class);
    }

    // ⭐ [핵심 추가] 애플리케이션 시작 시 시큐리티 컨텍스트 공유 전략을 변경합니다.
    @PostConstruct
    public void init() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
