package com.wiseai.meeting_reservation.dto;

public record PaymentRequest(
    String providerType, // CARD | SIMPLE | VIRTUAL_ACCOUNT
    String cardToken, // (옵션) 카드/간편결제 시뮬레이션용
    String simplePayUserId,
    String bankCode) {
}
