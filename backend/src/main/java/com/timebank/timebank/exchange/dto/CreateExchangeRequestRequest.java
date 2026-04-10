package com.timebank.timebank.exchange.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateExchangeRequestRequest {

    @NotBlank(message = "Mesaj boş olamaz")
    @Size(max = 1000, message = "Mesaj en fazla 1000 karakter olabilir")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}