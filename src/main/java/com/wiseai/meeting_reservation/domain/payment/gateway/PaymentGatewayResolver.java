package com.wiseai.meeting_reservation.domain.payment.gateway;

import com.wiseai.meeting_reservation.domain.payment.PaymentProviderType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentGatewayResolver {
  private final CardPaymentGateway card;
  private final SimplePaymentGateway simple;
  private final VirtualAccountPaymentGateway va;

  public PaymentGateway resolve(PaymentProviderType type) {
    return switch (type) {
      case CARD -> card;
      case SIMPLE -> simple;
      case VIRTUAL_ACCOUNT -> va;
    };
  }
}
