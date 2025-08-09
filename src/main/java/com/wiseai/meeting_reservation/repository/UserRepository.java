package com.wiseai.meeting_reservation.repository;

import com.wiseai.meeting_reservation.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByIdAndDeletedAtIsNull(Long id); // ← 소프트 삭제 반영

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
