package com.timebank.timebank.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserProfileRequest {

    @NotBlank(message = "Ad soyad boş olamaz")
    @Size(max = 100, message = "Ad soyad en fazla 100 karakter olabilir")
    private String fullName;

    @Size(max = 1000, message = "Bio en fazla 1000 karakter olabilir")
    private String bio;

    @Size(max = 30, message = "Telefon en fazla 30 karakter olabilir")
    private String phone;

    public String getFullName() {
        return fullName;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}