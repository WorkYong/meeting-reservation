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

  // reservations.reservation_id UNIQUE 설계 → @OneToOne
  @OneToOne(optional = false)
  @JoinColumn(name = "reservation_id", nullable = false, unique = true)
  private Reservation reservation;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider_type", nullable = false, length = 30)
  private PaymentProviderType providerType;

  @Column(nullable = false)
  private Integer amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentStatus status;

  @Column(name = "external_payment_id", unique = true, length = 100)
  private String externalPaymentId;

  public static Payment create(Reservation r, PaymentProviderType provider, int amount) {
    Payment p = new Payment();
    p.reservation = r;
    p.providerType = provider;
    p.amount = amount;
    p.status = PaymentStatus.PENDING;
    return p;
  }

  public void success(String externalId) {
    this.status = PaymentStatus.SUCCESS;
    this.externalPaymentId = externalId;
  }

  public void fail() {
    this.status = PaymentStatus.FAILED;
  }

  public void cancel() {
    this.status = PaymentStatus.CANCELLED;
  }
}
