package com.talha.lms.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${STRIPE_SECRET_KEY:}")
    private String secretKeyEnv;

    @PostConstruct
    public void init() {
        if (secretKeyEnv == null || secretKeyEnv.isBlank()) {
            throw new IllegalStateException("STRIPE_SECRET_KEY environment variable not set.");
        }
        Stripe.apiKey = secretKeyEnv;
    }
}