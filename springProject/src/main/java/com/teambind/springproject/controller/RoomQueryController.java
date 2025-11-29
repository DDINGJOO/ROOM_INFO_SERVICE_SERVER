package com.teambind.springproject.controller;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.KeywordResponse;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.service.RoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomQueryController {

	private final RoomQueryService roomQueryService;

	@GetMapping("/{roomId}")
	public ResponseEntity<RoomDetailResponse> getRoomById(@PathVariable Long roomId) {
		RoomDetailResponse response = roomQueryService.findRoomById(roomId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	public ResponseEntity<List<RoomSimpleResponse>> searchRooms(
			@RequestParam(required = false) String roomName,
			@RequestParam(required = false) List<Long> keywordIds,
			@RequestParam(required = false) Long placeId,
			@RequestParam(required = false) Integer minOccupancy
	) {
		RoomSearchQuery query = RoomSearchQuery.builder()
				.roomName(roomName)
				.keywordIds(keywordIds)
				.placeId(placeId)
				.minOccupancy(minOccupancy)
				.build();

		List<RoomSimpleResponse> responses = roomQueryService.searchRooms(query);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/place/{placeId}")
	public ResponseEntity<List<RoomSimpleResponse>> getRoomsByPlaceId(@PathVariable Long placeId) {
		List<RoomSimpleResponse> responses = roomQueryService.findRoomsByPlaceId(placeId);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/batch")
	public ResponseEntity<List<RoomDetailResponse>> getRoomsByIds(@RequestParam List<Long> ids) {
		List<RoomDetailResponse> responses = roomQueryService.findRoomsByIds(ids);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/keywords")
	public ResponseEntity<Map<Long, KeywordResponse>> getKeywordMap() {
		Map<Long, KeywordResponse> keywordMap = roomQueryService.getKeywordMap();
		return ResponseEntity.ok(keywordMap);
	}
}
