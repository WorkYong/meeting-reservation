package com.wiseai.meeting_reservation.repository;

import com.wiseai.meeting_reservation.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByReservationId(Long reservationId);

  Optional<Payment> findByExternalPaymentId(String externalPaymentId);
}
