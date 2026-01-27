package com.lumenmall.backend.controller;

import com.lumenmall.backend.service.PaypalService; // Make sure to create this
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
// Note: Global CORS in SecurityConfig usually handles this, but keeping it for now
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaypalService payPalService;

    public PaymentController(@Value("${stripe.api.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    // --- STRIPE ENDPOINT ---
    @PostMapping("/create-payment-intent")
    public Map<String, String> createPaymentIntent(@RequestBody Map<String, Object> data) throws StripeException {
        long amount = Math.round(Double.parseDouble(data.get("amount").toString()) * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .setReceiptEmail(data.get("email").toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return response;
    }

    // --- PAYPAL ENDPOINTS ---
    @PostMapping("/paypal/create")
    public ResponseEntity<?> createPayPalPayment(@RequestBody Map<String, Object> data) {
        try {
            Double amount = Double.parseDouble(data.get("amount").toString());
            // This returns the approval URL for the frontend to redirect to
            String approvalUrl = payPalService.createPayment(amount);
            return ResponseEntity.ok(Map.of("approvalUrl", approvalUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/paypal/capture")
    public ResponseEntity<?> capturePayPalPayment(@RequestParam("token") String orderId) {
        try {
            // This finalizes the transaction after the user approves it on PayPal
            Object response = payPalService.capturePayment(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}