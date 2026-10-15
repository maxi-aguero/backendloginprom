package com.apress.spring;

public class RegisterResponse {

    private Integer status;
    private String message;
    private String error;
    private String username;
    private String email;
    private String role;

    public RegisterResponse(){}

    public Integer getStatus(){return status;}
    public void setStatus(Integer status) {this.status = status;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getError() {return error;}
    public void setError(String error) {this.error = error;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
}
