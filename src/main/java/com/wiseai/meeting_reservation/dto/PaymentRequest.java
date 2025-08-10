package com.wiseai.meeting_reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 요청(모의) - 결제사 타입에 따라 필요한 필드만 채우면 됩니다.")
public record PaymentRequest(
    @Schema(description = "결제사 타입", example = "CARD", allowableValues = {
        "CARD", "SIMPLE", "VIRTUAL_ACCOUNT" }) String providerType, // CARD | SIMPLE | VIRTUAL_ACCOUNT

    @Schema(description = "카드/간편 결제용 토큰(옵션, CARD/SIMPLE에서 사용)", example = "tok_sample_123") String cardToken,

    @Schema(description = "간편결제 사용자 식별자(옵션, SIMPLE에서 사용)", example = "simple-user-001") String simplePayUserId,

    @Schema(description = "가상계좌 은행코드(옵션, VIRTUAL_ACCOUNT에서 사용)", example = "HANA") String bankCode){
}
