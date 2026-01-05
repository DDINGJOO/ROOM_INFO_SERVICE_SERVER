package com.teambind.springproject.controller;

import com.teambind.springproject.dto.query.RoomSearchQuery;
import com.teambind.springproject.dto.response.KeywordResponse;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.enums.AppType;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.service.RoomQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Query", description = "룸 조회/검색 API")
public class RoomQueryController {

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

	@GetMapping("/{roomId}")
	@Operation(summary = "룸 상세 조회", description = "ID로 룸의 상세 정보를 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<RoomDetailResponse> getRoomById(
			@Parameter(description = "앱 타입 (GENERAL: OPEN만 조회, PLACE_MANAGER: 모든 상태 조회)")
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		RoomDetailResponse response = roomQueryService.findRoomById(roomId, statusFilter);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	@Operation(summary = "룸 검색", description = "다양한 조건으로 룸을 검색합니다")
	@ApiResponse(responseCode = "200", description = "검색 성공")
	public ResponseEntity<List<RoomSimpleResponse>> searchRooms(
			@Parameter(description = "앱 타입")
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@Parameter(description = "룸 이름") @RequestParam(required = false) String roomName,
			@Parameter(description = "키워드 ID 목록") @RequestParam(required = false) List<Long> keywordIds,
			@Parameter(description = "공간 ID") @RequestParam(required = false) Long placeId,
			@Parameter(description = "최소 수용 인원") @RequestParam(required = false) Integer minOccupancy
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

	@GetMapping("/place/{placeId}")
	@Operation(summary = "공간별 룸 조회", description = "특정 공간에 속한 모든 룸을 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<List<RoomSimpleResponse>> getRoomsByPlaceId(
			@Parameter(description = "앱 타입")
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@Parameter(description = "공간 ID", required = true) @PathVariable Long placeId
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		List<RoomSimpleResponse> responses = roomQueryService.findRoomsByPlaceId(placeId, statusFilter);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/batch")
	@Operation(summary = "룸 일괄 조회", description = "여러 룸을 ID 목록으로 일괄 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<List<RoomDetailResponse>> getRoomsByIds(
			@Parameter(description = "앱 타입")
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@Parameter(description = "룸 ID 목록", required = true) @RequestParam List<Long> ids
	) {
		AppType appType = parseAppType(appTypeHeader);
		Status statusFilter = getStatusFilter(appType);
		List<RoomDetailResponse> responses = roomQueryService.findRoomsByIds(ids, statusFilter);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/keywords")
	@Operation(summary = "키워드 맵 조회", description = "모든 키워드 정보를 ID 기준으로 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	public ResponseEntity<Map<Long, KeywordResponse>> getKeywordMap() {
		Map<Long, KeywordResponse> keywordMap = roomQueryService.getKeywordMap();
		return ResponseEntity.ok(keywordMap);
	}
}
