package com.example.paymentreceiver.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String s) {
        super(s);
    }
}
