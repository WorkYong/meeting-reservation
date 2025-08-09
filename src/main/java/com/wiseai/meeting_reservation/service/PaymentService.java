package com.wiseai.meeting_reservation.service;

import com.wiseai.meeting_reservation.domain.payment.*;
import com.wiseai.meeting_reservation.domain.reservation.Reservation;
import com.wiseai.meeting_reservation.domain.reservation.ReservationStatus;
import com.wiseai.meeting_reservation.dto.PaymentRequest;
import com.wiseai.meeting_reservation.dto.PaymentResponse;
import com.wiseai.meeting_reservation.exception.ApiException;
import com.wiseai.meeting_reservation.exception.ErrorCode;
import com.wiseai.meeting_reservation.domain.payment.gateway.PaymentGateway;
import com.wiseai.meeting_reservation.domain.payment.gateway.PaymentGatewayResolver;
import com.wiseai.meeting_reservation.repository.PaymentRepository;
import com.wiseai.meeting_reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;
  private final PaymentGatewayResolver gatewayResolver;

  @Transactional
  public PaymentResponse pay(Long reservationId, PaymentRequest req) {
    Reservation r = reservationRepository.findByIdAndDeletedAtIsNull(reservationId)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found: " + reservationId));
    if (r.getStatus() != ReservationStatus.PENDING_PAYMENT)
      throw new ApiException(ErrorCode.INVALID_RESERVATION_STATUS, "결제 가능한 상태가 아닙니다.");

    PaymentProviderType provider = PaymentProviderType.valueOf(req.providerType());
    PaymentGateway gateway = gatewayResolver.resolve(provider);

    var payload = new HashMap<String, Object>();
    payload.put("amount", r.getTotalPrice());
    payload.put("orderId", "ORD-" + r.getId()); // A
    payload.put("cardToken", req.cardToken()); // A
    payload.put("simplePayUserId", req.simplePayUserId()); // B
    payload.put("merchantUid", "M-" + r.getId()); // B
    payload.put("bankCode", req.bankCode()); // C
    payload.put("ref", "R-" + r.getId()); // C

    PaymentResult result = gateway.pay(payload);

    Payment p = paymentRepository.findByReservationId(reservationId)
        .orElseGet(() -> Payment.create(r, provider, r.getTotalPrice()));

    switch (result.status()) {
      case SUCCESS -> {
        p.success(result.externalId());
        r.markPaid();
        r.confirm(); // 정책상 즉시 확정
      }
      case FAILED -> p.fail();
      case CANCELLED -> p.cancel();
      case PENDING -> {
        // 가상계좌 발급 등 — 예약은 여전히 PENDING_PAYMENT
        if (result.externalId() != null && p.getExternalPaymentId() == null) {
          // 필요하면 pending 상태 기록용 메서드를 별도 두어 외부 id 저장 가능
          // 여기서는 상태만 PENDING 유지
        }
      }
    }

    paymentRepository.save(p);
    return new PaymentResponse(
        p.getId(), r.getId(), p.getProviderType().name(),
        p.getStatus().name(), p.getExternalPaymentId(), p.getAmount());
  }

  @Transactional(readOnly = true)
  public PaymentResponse status(Long paymentId) {
    Payment p = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Payment not found: " + paymentId));
    return new PaymentResponse(
        p.getId(), p.getReservation().getId(), p.getProviderType().name(),
        p.getStatus().name(), p.getExternalPaymentId(), p.getAmount());
  }
}
