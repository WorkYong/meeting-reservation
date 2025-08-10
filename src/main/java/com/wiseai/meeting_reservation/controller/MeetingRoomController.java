package com.wiseai.meeting_reservation.controller;

import com.wiseai.meeting_reservation.dto.MeetingRoomsRequestDto;
import com.wiseai.meeting_reservation.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meeting Rooms", description = "회의실 조회 API")
@RestController
@RequestMapping(value = "/meeting-rooms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MeetingRoomController {

  private final MeetingRoomService meetingRoomService;

  @Operation(summary = "회의실 목록 조회 (result + 간단한 페이지 정보)", description = "소프트 삭제되지 않은 회의실을 페이지네이션하여 조회합니다.", responses = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Object.class), examples = @ExampleObject(name = "성공 예시", value = """
          {
            "result": [
              { "id": 1, "name": "Alpha", "capacity": 6, "hourlyPrice": 10000 },
              { "id": 2, "name": "Bravo", "capacity": 10, "hourlyPrice": 15000 }
            ],
            "pageNum": 1,
            "perPage": 10,
            "totalPages": 1
          }
          """)))
  })
  @GetMapping
  public ResponseEntity<?> list(@ParameterObject @Valid MeetingRoomsRequestDto request) {

    return ResponseEntity.ok(meetingRoomService.findAll(request));
  }
}
