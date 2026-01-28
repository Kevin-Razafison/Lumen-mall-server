package com.lumenmall.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- NEW METHOD FOR VERIFICATION ---
    public void sendVerificationEmail(String userEmail, String fullName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Point this to your LIVE frontend URL
            String verifyUrl = "https://lumen-mall-client.onrender.com/verify?token=" + token;

            helper.setTo(userEmail);
            helper.setSubject("Verify your Lumen Mall Account");

            String htmlContent = "<h3>Welcome to Lumen Mall, " + fullName + "!</h3>" +
                    "<p>Please click the button below to verify your email and activate your account:</p>" +
                    "<a href='" + verifyUrl + "' style='background-color: #febd69; color: black; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;'>Verify My Account</a>" +
                    "<p>If the button doesn't work, copy and paste this link:<br>" + verifyUrl + "</p>";

            helper.setText(htmlContent, true); // true = send as HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendLowStockAlert(String productName, int currentStock) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("kevinrazafison@gmail.com");
        message.setSubject("⚠️ LOW STOCK ALERT: " + productName);
        message.setText("Product: " + productName + "\n" + "Current Stock: " + currentStock);
        mailSender.send(message);
    }
}