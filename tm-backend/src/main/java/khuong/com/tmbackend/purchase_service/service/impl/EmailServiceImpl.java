package khuong.com.tmbackend.purchase_service.service.impl;

import org.springframework.stereotype.Service;

import khuong.com.tmbackend.purchase_service.dto.EmailNotificationDTO;
import khuong.com.tmbackend.purchase_service.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Override
    public void sendOrderConfirmationEmail(EmailNotificationDTO emailDTO) {
        // In a real implementation, this would use JavaMailSender or another email service
        // For now, we'll just log the email
        System.out.println("Sending order confirmation email to: " + emailDTO.getTo());
        System.out.println("Subject: " + emailDTO.getSubject());
        System.out.println("Order ID: " + emailDTO.getOrderId());
        // Additional email sending logic would go here
    }
} 