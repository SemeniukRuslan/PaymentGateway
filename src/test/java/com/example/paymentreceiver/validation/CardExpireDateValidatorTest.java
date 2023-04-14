package com.example.paymentreceiver.validation;

import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardExpireDateValidatorTest {

    private CardExpireDateValidator cardExpireDateValidator;

    @BeforeEach
    void setUp() {
        cardExpireDateValidator = new CardExpireDateValidator();
        // workaround dont like it...
        CardExpireDate c = new CardExpireDate(){
            @Override
            public String message() {
                return "Error";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String pattern() {
                return "MMyy";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };
        cardExpireDateValidator.initialize(c);
    }

    @Test
    void isValid() {
        String dateToWerify1 = "0624";
        String dateToWerify2 = "0621";
        String dateToWerify3 = "1125";
        String dateToWerify4 = "fswq";

        assertTrue(cardExpireDateValidator.isValid(dateToWerify1, null));
        assertFalse(cardExpireDateValidator.isValid(dateToWerify2, null));
        assertTrue(cardExpireDateValidator.isValid(dateToWerify3, null));
        assertFalse(cardExpireDateValidator.isValid(dateToWerify4, null));
    }
}