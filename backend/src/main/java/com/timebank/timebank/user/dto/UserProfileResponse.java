package com.timebank.timebank.user.dto;

import java.util.UUID;

public class UserProfileResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String bio;
    private String phone;
    private long timeCreditMinutes;

    public UserProfileResponse(UUID id, String fullName, String email, String bio, String phone, long timeCreditMinutes) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
        this.phone = phone;
        this.timeCreditMinutes = timeCreditMinutes;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    public long getTimeCreditMinutes() {
        return timeCreditMinutes;
    }
}