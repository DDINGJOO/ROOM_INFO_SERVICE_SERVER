package com.teambind.springproject.repository;

import com.teambind.springproject.entity.RoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInfoRepository extends JpaRepository<RoomInfo, Long> {
}
