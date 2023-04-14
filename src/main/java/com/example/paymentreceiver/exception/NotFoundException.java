package com.example.paymentreceiver.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotFoundException extends Exception {
    public NotFoundException(String s) {
        super(s);
    }
}
