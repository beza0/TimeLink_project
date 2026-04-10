package com.timebank.timebank.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    // --- Constructors ---

    protected User() {
        // Hibernate için gerekli
    }

    public User(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.timeCreditMinutes = 0;
        this.role = "USER";
    }

    // --- Fields ---

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String fullName;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    // TimeBank sistemi: dakika bazlı kredi
    @Column(nullable = false)
    private long timeCreditMinutes = 0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 20)
    private String role = "USER";

    @Column(length = 1000)
    private String bio;

    @Column(length = 30)
    private String phone;

    // --- Lifecycle Hooks ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- Getters ---

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getTimeCreditMinutes() {
        return timeCreditMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRole() {
        return role;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    // --- Setters (kontrollü bırakıyoruz) ---

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setTimeCreditMinutes(long timeCreditMinutes) {
        this.timeCreditMinutes = timeCreditMinutes;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
