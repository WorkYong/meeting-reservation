package com.wiseai.meeting_reservation.domain.payment;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;
import com.wiseai.meeting_reservation.domain.reservation.Reservation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private PaymentProviderType providerType;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private Integer amount;

  private String externalPaymentId;

  @OneToOne
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;
}
