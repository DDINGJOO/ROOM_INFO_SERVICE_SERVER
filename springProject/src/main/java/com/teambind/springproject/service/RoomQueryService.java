package com.teambind.springproject.service;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.mapper.RoomQueryMapper;
import com.teambind.springproject.repository.RoomQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {
	
	private final RoomQueryRepository roomQueryRepository;
	private final RoomQueryMapper roomQueryMapper;
	
	public List<RoomSimpleResponse> searchRooms(RoomSearchQuery query) {
		List<RoomInfo> rooms = roomQueryRepository.findAllByQuery(query);
		return roomQueryMapper.toSimpleResponseList(rooms);
	}
	
	public List<RoomSimpleResponse> findRoomsByPlaceId(Long placeId) {
		RoomSearchQuery query = RoomSearchQuery.builder()
				.placeId(placeId)
				.build();
		List<RoomInfo> rooms = roomQueryRepository.findAllByQuery(query);
		return roomQueryMapper.toSimpleResponseList(rooms);
	}
	
	public List<RoomSimpleResponse> findRoomsByName(String roomName) {
		RoomSearchQuery query = RoomSearchQuery.builder()
				.roomName(roomName)
				.build();
		List<RoomInfo> rooms = roomQueryRepository.findAllByQuery(query);
		return roomQueryMapper.toSimpleResponseList(rooms);
	}
	
	public List<RoomSimpleResponse> findRoomsByKeywords(List<Long> keywordIds) {
		RoomSearchQuery query = RoomSearchQuery.builder()
				.keywordIds(keywordIds)
				.build();
		List<RoomInfo> rooms = roomQueryRepository.findAllByQuery(query);
		return roomQueryMapper.toSimpleResponseList(rooms);
	}
	
	public List<RoomDetailResponse> findRoomsByIds(List<Long> roomIds) {
		List<RoomInfo> rooms = roomQueryRepository.findByIdsWithDetails(roomIds);
		return roomQueryMapper.toDetailResponseList(rooms);
	}
	
	public RoomDetailResponse findRoomById(Long roomId) {
		RoomInfo room = roomQueryRepository.findByIdWithDetails(roomId)
				.orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
		return roomQueryMapper.toDetailResponse(room);
	}
}
