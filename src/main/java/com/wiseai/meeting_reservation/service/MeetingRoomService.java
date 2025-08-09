package com.wiseai.meeting_reservation.service;

import com.wiseai.meeting_reservation.domain.meetingroom.MeetingRoom;
import com.wiseai.meeting_reservation.dto.MeetingRoomItemDto;

import com.wiseai.meeting_reservation.dto.MeetingRoomsRequestDto;
import com.wiseai.meeting_reservation.dto.MeetingRoomsResponseDto;
import com.wiseai.meeting_reservation.repository.MeetingRoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

  private final MeetingRoomRepository meetingRoomRepository;

  public MeetingRoomsResponseDto findAll(MeetingRoomsRequestDto req) {
    // 기본값 보정
    int pageNum = (req.pageNum() == null || req.pageNum() < 1) ? 1 : req.pageNum();
    int perPage = (req.perPage() == null || req.perPage() < 1) ? 10 : req.perPage();

    // 정렬 구성
    String sortProp = (req.sort() == null || req.sort().isBlank()) ? "hourlyPrice" : req.sort();
    Sort.Direction dir = "desc".equalsIgnoreCase(req.direction()) ? Sort.Direction.DESC : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(pageNum - 1, perPage, Sort.by(dir, sortProp));

    Page<MeetingRoom> page = (req.minCapacity() == null)
        ? meetingRoomRepository.findByDeletedAtIsNull(pageable)
        : meetingRoomRepository.findByDeletedAtIsNullAndCapacityGreaterThanEqual(req.minCapacity(), pageable);

    List<MeetingRoomItemDto> items = page.getContent().stream()
        .map(m -> new MeetingRoomItemDto(m.getId(), m.getName(), m.getCapacity(), m.getHourlyPrice()))
        .toList();

    return new MeetingRoomsResponseDto(
        items,
        pageNum,
        perPage,
        page.getTotalPages());
  };
}
