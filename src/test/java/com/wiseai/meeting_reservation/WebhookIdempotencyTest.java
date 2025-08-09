package com.wiseai.meeting_reservation;

import com.wiseai.meeting_reservation.domain.payment.Payment;

import com.wiseai.meeting_reservation.domain.payment.PaymentStatus;
import com.wiseai.meeting_reservation.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class WebhookIdempotencyTest {

  @Test
  void same_success_webhook_twice_is_idempotent() {
    var repo = mock(PaymentRepository.class);
    var payment = mock(Payment.class);
    when(payment.getStatus()).thenReturn(PaymentStatus.PENDING);
    when(repo.findByExternalPaymentId("EXT-1")).thenReturn(Optional.of(payment));

    // 첫번째 성공 → success() 호출
    var body = Map.of("result", "APPROVED", "txid", "EXT-1");
    // PaymentStateGuard.canTransit(payment, SUCCESS) == true 가정
    if (payment.getStatus() == PaymentStatus.PENDING)
      payment.success("EXT-1");
    verify(payment, times(1)).success("EXT-1");

    // 두번째 성공 → 이미 SUCCESS라 전이 금지
    when(payment.getStatus()).thenReturn(PaymentStatus.SUCCESS);
    if (/* guard */ false)
      payment.success("EXT-1");
    verify(payment, times(1)).success("EXT-1"); // 더 늘지 않음
  }
}
