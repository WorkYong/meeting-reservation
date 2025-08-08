package com.wiseai.meeting_reservation.domain.provider;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;
import com.wiseai.meeting_reservation.domain.payment.PaymentProviderType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_providers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentProvider extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String apiEndpoint;

  private String authKey;

  @Enumerated(EnumType.STRING)
  private PaymentProviderType providerType;
}
