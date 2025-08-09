package com.wiseai.meeting_reservation.service;

import com.wiseai.meeting_reservation.domain.reservation.Reservation;
import com.wiseai.meeting_reservation.domain.reservation.ReservationStatus;

import com.wiseai.meeting_reservation.dto.ReservationCreateRequest;
import com.wiseai.meeting_reservation.dto.ReservationResponse;
import com.wiseai.meeting_reservation.dto.ReservationUpdateRequest;
import com.wiseai.meeting_reservation.exception.ApiException;
import com.wiseai.meeting_reservation.exception.ErrorCode;
import com.wiseai.meeting_reservation.repository.MeetingRoomRepository;
import com.wiseai.meeting_reservation.repository.ReservationRepository;
import com.wiseai.meeting_reservation.repository.UserRepository;
import com.wiseai.meeting_reservation.util.DateTimeRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final MeetingRoomRepository meetingRoomRepository;
  private final UserRepository userRepository;

  @Transactional
  public ReservationResponse create(ReservationCreateRequest req) {
    try {
      DateTimeRules.validate(req.startTime(), req.endTime());
    } catch (IllegalArgumentException e) {
      throw new ApiException(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    var room = meetingRoomRepository.findById(req.meetingRoomId())
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "MeetingRoom not found"));
    var user = userRepository.findById(req.userId())
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));

    var active = List.of(ReservationStatus.PENDING_PAYMENT, ReservationStatus.PAID, ReservationStatus.CONFIRMED);
    var conflicts = reservationRepository.findOverlappedForUpdate(room.getId(), req.startTime(), req.endTime(), active);
    if (!conflicts.isEmpty())
      throw new ApiException(ErrorCode.TIME_SLOT_ALREADY_BOOKED, "이미 예약된 시간대입니다.");

    int total = DateTimeRules.calcTotalPrice(room.getHourlyPrice(), req.startTime(), req.endTime());

    var r = Reservation.create(
        user,
        room,
        req.startTime(),
        req.endTime(),
        ReservationStatus.PENDING_PAYMENT,
        total);
    reservationRepository.save(r);

    return toDto(r);
  }

  @Transactional(readOnly = true)
  public ReservationResponse get(Long id) {
    var r = reservationRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found"));
    return toDto(r);
  }

  @Transactional
  public ReservationResponse cancel(Long id) {
    var r = reservationRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found: " + id));

    if (!r.isDeleted()) {
      r.softDelete();
      reservationRepository.save(r);
    }
    return toDto(r);
  }

  @Transactional
  public ReservationResponse update(Long id, ReservationUpdateRequest request) {

    var reservation = reservationRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Reservation not found: " + id));

    var targetRoom = (request.getMeetingRoomId() != null)
        ? meetingRoomRepository.findByIdAndDeletedAtIsNull(request.getMeetingRoomId())
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,
                "MeetingRoom not found: " + request.getMeetingRoomId()))
        : reservation.getMeetingRoom();

    var newStart = (request.getStartTime() != null) ? request.getStartTime() : reservation.getStartTime();
    var newEnd = (request.getEndTime() != null) ? request.getEndTime() : reservation.getEndTime();

    DateTimeRules.validate(newStart, newEnd);

    var active = java.util.List.of(
        ReservationStatus.PENDING_PAYMENT,
        ReservationStatus.CONFIRMED,
        ReservationStatus.PAID

    );
    var conflicts = reservationRepository.findOverlappedForUpdate(
        targetRoom.getId(), newStart, newEnd, active);
    conflicts.removeIf(c -> c.getId().equals(reservation.getId()));
    if (!conflicts.isEmpty()) {
      throw new ApiException(ErrorCode.TIME_SLOT_ALREADY_BOOKED, "이미 예약된 시간대입니다.");
    }

    if (!targetRoom.getId().equals(reservation.getMeetingRoom().getId())) {
      reservation.changeMeetingRoom(targetRoom);
    }
    if (!newStart.equals(reservation.getStartTime()) || !newEnd.equals(reservation.getEndTime())) {
      reservation.reschedule(newStart, newEnd);
      int newTotal = DateTimeRules.calcTotalPrice(targetRoom.getHourlyPrice(), newStart, newEnd);
      reservation.changeTotalPrice(newTotal);
    }

    var saved = reservationRepository.save(reservation);
    return toDto(saved);
  }

  private ReservationResponse toDto(Reservation r) {
    return ReservationResponse.builder()
        .id(r.getId())
        .userId(r.getUser().getId())
        .meetingRoomId(r.getMeetingRoom().getId())
        .startTime(r.getStartTime())
        .endTime(r.getEndTime())
        .status(r.getStatus().name())
        .totalPrice(r.getTotalPrice())
        .build();
  }

}
