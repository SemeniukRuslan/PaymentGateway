package com.example.paymentreceiver.configuration;

import com.example.paymentreceiver.dto.ResponseHolder;
import com.example.paymentreceiver.exception.AlreadyExistsException;
import com.example.paymentreceiver.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(BindException.class)
    public static ResponseEntity<ResponseHolder> applicationException(BindException exception) {

        final Map<String, String> errors = exception
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        el -> el.getField(),
                        el -> el.getDefaultMessage(),
                        (existingValue, newValue) -> existingValue
                ));

        log.error("ValidationException", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseHolder.builder().approved(false).errors(errors).build());
    }

    @ExceptionHandler(NotFoundException.class)
    public static ResponseEntity<ResponseHolder> paymentNotFound(NotFoundException exception) {
        log.error("PaymentWithInvoiceNumberNotFound - ", exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseHolder.builder().approved(false)
                        .errors(Map.of("invoice", "payment with invoice number not found")).build());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public static ResponseEntity<ResponseHolder> paymentAlreadyExistsException(Exception exception) {
        log.error("PaymentWithInvoiceAlreadyExists - ", exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseHolder.builder().approved(false)
                        .errors(Map.of("invoice", "payment with invoice already exists")).build());
    }
}
