package com.example.paymentreceiver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ResponseHolder {

    private boolean approved;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
}