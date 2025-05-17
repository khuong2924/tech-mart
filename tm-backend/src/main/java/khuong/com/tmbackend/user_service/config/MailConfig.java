package khuong.com.tmbackend.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
        // Spring Boot sẽ tự động cấu hình JavaMailSenderImpl từ các thuộc tính trong application.yml
    }
} 