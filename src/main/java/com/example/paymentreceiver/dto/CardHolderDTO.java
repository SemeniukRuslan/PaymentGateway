package com.example.paymentreceiver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardHolderDTO {

    @NotBlank(message = "name should be not empty")
    private String name;

    @Email(message = "email should be in form email@example.com")
    @NotBlank(message = "email should be not empty")
    private String email;
}
