package com.example.paymentreceiver.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    @NotNull
    @Positive(message = "invoice should be more than zero")
    private int invoice;

    @Positive(message = "amount should be more than zero")
    private int amount;

    @NotBlank(message = "currency should be not empty")
    private String currency;

    @NotNull
    @Valid
    private CardHolderDTO cardHolder;

    @NotNull
    @Valid
    private CardDTO card;
}
