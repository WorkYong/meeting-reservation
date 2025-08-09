package com.wiseai.meeting_reservation.domain.payment.gateway;

import com.wiseai.meeting_reservation.domain.payment.PaymentResult;
import java.util.Map;

public interface PaymentGateway {
  PaymentResult pay(Map<String, Object> payload);

  PaymentResult parseWebhook(Map<String, Object> body);
}