package com.apress.spring;

public class LoginResponse {
    private String token;
    private String username;
    private String role;

    private String message;
    private Integer status;
    private String error;

    public LoginResponse() {   }

    // Getters y setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
