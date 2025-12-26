package com.teambind.springproject.repository;

import com.teambind.springproject.entity.attribute.ReservationField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationFieldRepository extends JpaRepository<ReservationField, Long> {

    List<ReservationField> findByRoomInfoRoomIdOrderBySequenceAsc(Long roomId);

    void deleteByRoomInfoRoomId(Long roomId);
}