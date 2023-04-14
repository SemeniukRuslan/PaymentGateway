package com.example.paymentreceiver.dto;

import com.example.paymentreceiver.validation.CardExpireDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.LuhnCheck;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    @CreditCardNumber
    @LuhnCheck
    @NotNull(message = "pan should be not empty")
    private String pan;

    @CardExpireDate
    @NotBlank(message = "expiryDate should be not empty")
    private String expiryDate;

    @NotBlank(message = "cvv should be not empty")
    private String cvv;
}
