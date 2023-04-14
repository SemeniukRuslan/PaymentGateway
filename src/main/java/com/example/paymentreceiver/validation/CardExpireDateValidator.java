package com.example.paymentreceiver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

@Slf4j
public class CardExpireDateValidator implements ConstraintValidator<CardExpireDate, String> {

    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(CardExpireDate contactNumber) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(contactNumber.pattern());
    }

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        try {
            if (Objects.isNull(contactField))
                return false;

            final TemporalAccessor parsedExpiry = dateTimeFormatter.parse(contactField);
            final int month = parsedExpiry.get(ChronoField.MONTH_OF_YEAR);
            final int year = parsedExpiry.get(ChronoField.YEAR);
            final LocalDate expireDate = LocalDate.of(year, month, 1);

            return !expireDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException ex) {
            log.error("'card.expiry' is not parsable parse");
            return false;
        }
    }
}
