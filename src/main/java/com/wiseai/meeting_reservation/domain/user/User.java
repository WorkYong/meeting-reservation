package com.wiseai.meeting_reservation.domain.user;

import com.wiseai.meeting_reservation.domain.base.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String email;

  private String phoneNumber;

  private Integer level;

  private String isDeleted;
}
