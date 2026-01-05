package com.teambind.springproject.controller.swagger;

import com.teambind.springproject.dto.response.KeywordResponse;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Tag(name = "Room Query", description = "룸 조회/검색 API")
public interface RoomQueryControllerSwagger {

	@Operation(summary = "룸 상세 조회", description = "ID로 룸의 상세 정보를 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<RoomDetailResponse> getRoomById(
			@Parameter(description = "앱 타입 (GENERAL: OPEN만 조회, PLACE_MANAGER: 모든 상태 조회)") String appTypeHeader,
			@Parameter(description = "룸 ID", required = true) Long roomId
	);

	@Operation(summary = "룸 검색", description = "다양한 조건으로 룸을 검색합니다")
	@ApiResponse(responseCode = "200", description = "검색 성공")
	ResponseEntity<List<RoomSimpleResponse>> searchRooms(
			@Parameter(description = "앱 타입") String appTypeHeader,
			@Parameter(description = "룸 이름") String roomName,
			@Parameter(description = "키워드 ID 목록") List<Long> keywordIds,
			@Parameter(description = "공간 ID") Long placeId,
			@Parameter(description = "최소 수용 인원") Integer minOccupancy
	);

	@Operation(summary = "공간별 룸 조회", description = "특정 공간에 속한 모든 룸을 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	ResponseEntity<List<RoomSimpleResponse>> getRoomsByPlaceId(
			@Parameter(description = "앱 타입") String appTypeHeader,
			@Parameter(description = "공간 ID", required = true) Long placeId
	);

	@Operation(summary = "룸 일괄 조회", description = "여러 룸을 ID 목록으로 일괄 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	ResponseEntity<List<RoomDetailResponse>> getRoomsByIds(
			@Parameter(description = "앱 타입") String appTypeHeader,
			@Parameter(description = "룸 ID 목록", required = true) List<Long> ids
	);

	@Operation(summary = "키워드 맵 조회", description = "모든 키워드 정보를 ID 기준으로 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	ResponseEntity<Map<Long, KeywordResponse>> getKeywordMap();
}
