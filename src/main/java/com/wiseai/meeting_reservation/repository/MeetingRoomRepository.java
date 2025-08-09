package com.wiseai.meeting_reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.wiseai.meeting_reservation.domain.meetingroom.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
  Page<MeetingRoom> findByDeletedAtIsNull(Pageable pageable);

  Page<MeetingRoom> findByDeletedAtIsNullAndCapacityGreaterThanEqual(int capacity, Pageable pageable);

  Optional<MeetingRoom> findByIdAndDeletedAtIsNull(Long id);
}
