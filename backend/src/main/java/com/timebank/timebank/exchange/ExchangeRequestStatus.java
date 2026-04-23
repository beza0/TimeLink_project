package com.timebank.timebank.exchange;

public enum ExchangeRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    /** Talep sahibi geri çekti veya taraflardan biri onaylanmış oturumu oturum saatinden önce iptal etti */
    CANCELLED,
    COMPLETED
}