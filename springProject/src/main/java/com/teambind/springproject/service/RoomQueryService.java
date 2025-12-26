package com.teambind.springproject.service;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.KeywordResponse;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.exception.RoomNotFoundException;
import com.teambind.springproject.mapper.RoomQueryMapper;
import com.teambind.springproject.repository.RoomQueryRepository;
import com.teambind.springproject.util.data.InitialTableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public List<RoomSimpleResponse> findRoomsByPlaceId(Long placeId, Status status) {
		RoomSearchQuery query = RoomSearchQuery.builder()
				.placeId(placeId)
				.status(status)
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

	public List<RoomDetailResponse> findRoomsByIds(List<Long> roomIds, Status status) {
		List<RoomInfo> rooms = roomQueryRepository.findByIdsWithDetails(roomIds);
		if (status != null) {
			rooms = rooms.stream()
					.filter(room -> room.getStatus() == status)
					.collect(Collectors.toList());
		}
		return roomQueryMapper.toDetailResponseList(rooms);
	}

	public RoomDetailResponse findRoomById(Long roomId, Status requiredStatus) {
		RoomInfo room = roomQueryRepository.findByIdWithDetails(roomId)
				.orElseThrow(() -> new RoomNotFoundException(roomId));
		if (requiredStatus != null && room.getStatus() != requiredStatus) {
			throw new RoomNotFoundException(roomId);
		}
		return roomQueryMapper.toDetailResponse(room);
	}
	
	public Map<Long, KeywordResponse> getKeywordMap() {
		return InitialTableMapper.keywordMap.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> Long.valueOf(entry.getKey()),
						entry -> KeywordResponse.builder()
								.keywordId(entry.getValue().getKeywordId())
								.keyword(entry.getValue().getKeyword())
								.build()
				));
	}
}
