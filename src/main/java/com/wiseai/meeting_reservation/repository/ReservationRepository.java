package com.wiseai.meeting_reservation.repository;

import com.wiseai.meeting_reservation.domain.reservation.Reservation;
import com.wiseai.meeting_reservation.domain.reservation.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
          select r from Reservation r
          where r.meetingRoom.id = :roomId
            and r.deletedAt is null
            and r.status in (:active)
            and r.startTime < :end
            and r.endTime   > :start
      """)
  List<Reservation> findOverlappedForUpdate(@Param("roomId") Long roomId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("active") List<ReservationStatus> active);

  Optional<Reservation> findByIdAndDeletedAtIsNull(Long id);
}
