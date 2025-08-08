package com.wiseai.meeting_reservation.domain.reservation;

import java.time.LocalDateTime;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;
import com.wiseai.meeting_reservation.domain.meetingroom.MeetingRoom;
import com.wiseai.meeting_reservation.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  private Integer totalPrice;

  @ManyToOne
  @JoinColumn(name = "meeting_room_id")
  private MeetingRoom meetingRoom;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
