package com.lumenmall.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendLowStockAlert(String productName, int currentStock) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("your-admin-email@gmail.com"); // Where the alerts go
        message.setSubject("⚠️ LOW STOCK ALERT: " + productName);
        message.setText("The following product is running low on stock:\n\n" +
                "Product: " + productName + "\n" +
                "Current Stock: " + currentStock + "\n\n" +
                "Please restock soon via the Admin Dashboard.");

        mailSender.send(message);
    }
}