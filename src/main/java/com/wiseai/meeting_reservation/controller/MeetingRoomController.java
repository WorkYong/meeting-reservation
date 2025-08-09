package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.dto.MeetingRoomsRequestDto;
import com.wiseai.meeting_reservation.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Meeting Rooms", description = "회의실 조회 API")
@RestController
@RequestMapping("/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

  private final MeetingRoomService meetingRoomService;

  @Operation(summary = "회의실 목록 조회 (result + 간단한 페이지 정보)")
  @GetMapping
  public ResponseEntity<?> list(@Valid MeetingRoomsRequestDto request) {
    return ResponseEntity.ok(meetingRoomService.findAll(request));
  }
}
