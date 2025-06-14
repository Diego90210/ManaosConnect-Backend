package com.diego.gestorcasino.dto;

public class RegistroConsumidorResponseDTO {
    private boolean success;
    private String message;

    // Constructor, getters y setters
    public RegistroConsumidorResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
