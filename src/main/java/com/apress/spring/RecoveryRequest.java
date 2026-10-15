package com.apress.spring;


public class RecoveryRequest {
    private String email;

    // Constructor vacío
    public RecoveryRequest() {}

    // Constructor con parámetros
    public RecoveryRequest(String email) {
        this.email = email;
    }

    // Getter y Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
