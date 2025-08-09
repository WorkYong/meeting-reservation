
package com.wiseai.meeting_reservation.domain.reservation;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;
import com.wiseai.meeting_reservation.domain.meetingroom.MeetingRoom;
import com.wiseai.meeting_reservation.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status;

  @Column(name = "total_price", nullable = false)
  private Integer totalPrice;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "meeting_room_id", nullable = false)
  private MeetingRoom meetingRoom;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 정적 팩토리
  public static Reservation create(User user,
      MeetingRoom room,
      LocalDateTime start,
      LocalDateTime end,
      ReservationStatus initialStatus,
      int totalPrice) {
    Reservation r = new Reservation();
    r.user = user;
    r.meetingRoom = room;
    r.startTime = start;
    r.endTime = end;
    r.status = initialStatus;
    r.totalPrice = totalPrice;
    return r;
  }

  public void markPending() {
    this.status = ReservationStatus.PENDING_PAYMENT;
  }

  public void markPaid() {
    this.status = ReservationStatus.PAID;
  }

  public void confirm() {
    this.status = ReservationStatus.CONFIRMED;
  }

  public void cancel() {
    this.status = ReservationStatus.CANCELLED;
  }

  public void noShow() {
    this.status = ReservationStatus.NO_SHOW;
  }

  public void softDelete() {
    this.status = ReservationStatus.CANCELLED;
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public void changeMeetingRoom(MeetingRoom room) {
    this.meetingRoom = room;
  }

  public void reschedule(LocalDateTime start, LocalDateTime end) {
    this.startTime = start;
    this.endTime = end;
  }

  public void changeTotalPrice(int price) {
    this.totalPrice = price;
  }
}
