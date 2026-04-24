package com.timebank.timebank.exchange.dto;

import jakarta.validation.constraints.Size;

/**
 * Kabul edilmiş oturum için toplantı odası / link (Zoom, Meet, vb.).
 */
public class UpdateSessionMeetingRequest {

    @Size(max = 2000)
    private String meetingUrl;

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }
}
