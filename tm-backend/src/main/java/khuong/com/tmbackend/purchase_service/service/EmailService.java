package khuong.com.tmbackend.purchase_service.service;

import khuong.com.tmbackend.purchase_service.dto.EmailNotificationDTO;

public interface EmailService {
    void sendOrderConfirmationEmail(EmailNotificationDTO emailDTO);
} 