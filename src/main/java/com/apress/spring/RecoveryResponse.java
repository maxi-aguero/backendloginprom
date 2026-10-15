package com.apress.spring;


public class RecoveryResponse {
    private String message;
    private Integer status;
    private String error;

    // Opcional: puedes incluir el email del usuario o el token si quieres debug
    private String email;
    private String token;

    // Getters y setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
