package com.leonardorick.olx_clone;

import com.google.firebase.firestore.Exclude;

public class User {
    private String email;
    private String password;

    public User() {}
    /******* getters and setters *******/
    public String getEmail() { return email; }
    @Exclude
    public String getPassword() { return password; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}