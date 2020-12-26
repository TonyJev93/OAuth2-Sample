package com.tonyjev93.oauth2.ex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
public class Oauth2SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2SampleApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("사용자이름.. 실무에서는 세션이나 스프링 시큐리티 로그인 정보 이용");
    }
}
