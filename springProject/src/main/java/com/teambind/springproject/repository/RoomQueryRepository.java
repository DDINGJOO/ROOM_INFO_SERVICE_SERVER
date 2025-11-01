package com.teambind.springproject.repository;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.entity.RoomInfo;

import java.util.List;
import java.util.Optional;

public interface RoomQueryRepository {
	
	List<RoomInfo> findAllByQuery(RoomSearchQuery query);
	
	Optional<RoomInfo> findByIdWithDetails(Long roomId);
	
	List<RoomInfo> findByIdsWithDetails(List<Long> roomIds);
}
