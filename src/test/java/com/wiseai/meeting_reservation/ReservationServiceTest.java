package com.wiseai.meeting_reservation;

import com.wiseai.meeting_reservation.domain.meetingroom.MeetingRoom;
import com.wiseai.meeting_reservation.domain.reservation.Reservation;

import com.wiseai.meeting_reservation.domain.user.User;
import com.wiseai.meeting_reservation.dto.ReservationCreateRequest;
import com.wiseai.meeting_reservation.exception.ApiException;
import com.wiseai.meeting_reservation.repository.MeetingRoomRepository;
import com.wiseai.meeting_reservation.repository.ReservationRepository;
import com.wiseai.meeting_reservation.repository.UserRepository;
import com.wiseai.meeting_reservation.service.ReservationService;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

  ReservationRepository reservationRepository = mock(ReservationRepository.class);
  MeetingRoomRepository meetingRoomRepository = mock(MeetingRoomRepository.class);
  UserRepository userRepository = mock(UserRepository.class);
  ReservationService service = new ReservationService(reservationRepository, meetingRoomRepository, userRepository);

  @Test
  void create_conflict_throws() {
    long meetingRoomId = 10L;
    long userId = 1L;

    var user = mock(User.class);
    when(user.getId()).thenReturn(userId);
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

    var room = mock(MeetingRoom.class);
    when(room.getId()).thenReturn(meetingRoomId);
    when(room.getHourlyPrice()).thenReturn(10000);
    when(meetingRoomRepository.findByIdAndDeletedAtIsNull(meetingRoomId)).thenReturn(Optional.of(room));

    var start = LocalDateTime.of(2025, 8, 10, 10, 0);
    var end = start.plusHours(1);

    var existing = mock(Reservation.class);
    when(existing.getId()).thenReturn(999L);

    when(reservationRepository.findOverlappedForUpdate(eq(meetingRoomId), eq(start), eq(end), anyList()))
        .thenReturn(List.of(existing));

    var req = new ReservationCreateRequest(meetingRoomId, userId, start, end);

    assertThrows(ApiException.class, () -> service.create(req));
    verify(reservationRepository, never()).save(any());
  }

  @Test
  void create_ok_saves() {
    long meetingRoomId = 10L;
    long userId = 1L;

    var user = mock(User.class);
    when(user.getId()).thenReturn(userId);
    when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // 방어

    var room = mock(MeetingRoom.class);
    when(room.getId()).thenReturn(meetingRoomId);
    when(room.getHourlyPrice()).thenReturn(10000);
    when(meetingRoomRepository.findByIdAndDeletedAtIsNull(meetingRoomId)).thenReturn(Optional.of(room));
    when(meetingRoomRepository.findById(meetingRoomId)).thenReturn(Optional.of(room)); // 방어

    var start = LocalDateTime.of(2025, 8, 10, 10, 0);
    var end = start.plusHours(2);

    when(reservationRepository.findOverlappedForUpdate(eq(meetingRoomId), eq(start), eq(end), anyList()))
        .thenReturn(List.of()); // 겹침 없음

    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var req = new ReservationCreateRequest(userId, meetingRoomId, start, end);

    var res = service.create(req);

    assertNotNull(res);
    assertEquals(meetingRoomId, res.getMeetingRoomId());
    assertEquals(userId, res.getUserId());
    assertEquals(start, res.getStartTime());
    assertEquals(end, res.getEndTime());
    verify(reservationRepository).save(any(Reservation.class));
  }
}
