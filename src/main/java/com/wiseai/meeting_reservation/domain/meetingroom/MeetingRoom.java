package com.wiseai.meeting_reservation.domain.meetingroom;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoom extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Integer capacity;

  private Integer hourlyPrice;
}
