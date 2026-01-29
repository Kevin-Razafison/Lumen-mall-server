package com.lumenmall.backend.service;

import com.resend.*;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        Resend resend = new Resend(resendApiKey);

        String verificationUrl = "https://lumen-mall-client.onrender.com/verify?token=" + token;

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Lumen Mall <onboarding@resend.dev>") // Use this for testing
                .to(toEmail)
                .subject("Verify your Lumen Mall Account")
                .html("<h1>Welcome, " + fullName + "!</h1>" +
                        "<p>Please click the link below to verify your email:</p>" +
                        "<a href=\"" + verificationUrl + "\">Verify Account</a>")
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("Email sent successfully: " + response.getId());
        } catch (Exception e) {
            System.err.println("Resend Error: " + e.getMessage());
            // We throw the error so the Controller can catch it
            throw new RuntimeException("Failed to send email via Resend");
        }
    }
}