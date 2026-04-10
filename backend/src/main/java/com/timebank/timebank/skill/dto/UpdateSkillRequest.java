package com.timebank.timebank.skill.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateSkillRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 100, message = "Başlık en fazla 100 karakter olabilir")
    private String title;

    @NotBlank(message = "Açıklama boş olamaz")
    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String description;

    @Min(value = 30, message = "Süre en az 30 dakika olmalı")
    private int durationMinutes;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}