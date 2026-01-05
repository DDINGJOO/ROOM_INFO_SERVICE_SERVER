package com.teambind.springproject.controller;

import com.teambind.springproject.controller.swagger.RoomQueryControllerSwagger;
import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.KeywordResponse;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.enums.AppType;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.service.RoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomQueryController implements RoomQueryControllerSwagger {

	private static final String HEADER_APP_TYPE = "X-App-Type";

	private final RoomQueryService roomQueryService;

	private AppType parseAppType(String appTypeHeader) {
		if (appTypeHeader == null || appTypeHeader.isBlank()) {
			return AppType.GENERAL;
		}
		try {
			return AppType.valueOf(appTypeHeader);
		} catch (IllegalArgumentException e) {
			return AppType.GENERAL;
		}
	}

	private Status getStatusFilter(AppType appType) {
		return appType == AppType.PLACE_MANAGER ? null : Status.OPEN;
	}

	@Override
	@GetMapping("/{roomId}")
	public ResponseEntity<RoomDetailResponse> getRoomById(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@PathVariable Long roomId
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		RoomDetailResponse response = roomQueryService.findRoomById(roomId, statusFilter);
		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/search")
	public ResponseEntity<List<RoomSimpleResponse>> searchRooms(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestParam(required = false) String roomName,
			@RequestParam(required = false) List<Long> keywordIds,
			@RequestParam(required = false) Long placeId,
			@RequestParam(required = false) Integer minOccupancy
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);

		RoomSearchQuery query = RoomSearchQuery.builder()
				.roomName(roomName)
				.keywordIds(keywordIds)
				.placeId(placeId)
				.minOccupancy(minOccupancy)
				.status(statusFilter)
				.build();

		List<RoomSimpleResponse> responses = roomQueryService.searchRooms(query);
		return ResponseEntity.ok(responses);
	}

	@Override
	@GetMapping("/place/{placeId}")
	public ResponseEntity<List<RoomSimpleResponse>> getRoomsByPlaceId(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@PathVariable Long placeId
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		List<RoomSimpleResponse> responses = roomQueryService.findRoomsByPlaceId(placeId, statusFilter);
		return ResponseEntity.ok(responses);
	}

	@Override
	@GetMapping("/batch")
	public ResponseEntity<List<RoomDetailResponse>> getRoomsByIds(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestParam List<Long> ids
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		List<RoomDetailResponse> responses = roomQueryService.findRoomsByIds(ids, statusFilter);
		return ResponseEntity.ok(responses);
	}

	@Override
	@GetMapping("/keywords")
	public ResponseEntity<Map<Long, KeywordResponse>> getKeywordMap() {
		Map<Long, KeywordResponse> keywordMap = roomQueryService.getKeywordMap();
		return ResponseEntity.ok(keywordMap);
	}
}
