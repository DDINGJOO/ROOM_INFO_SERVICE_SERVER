package com.teambind.springproject.controller;

import com.teambind.springproject.controller.annotation.RequireRoomManager;
import com.teambind.springproject.controller.annotation.ValidateRoomOwnership;
import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import com.teambind.springproject.service.ReservationFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms/{roomId}/reservation-fields")
@RequiredArgsConstructor
@Tag(name = "Reservation Field", description = "예약 필드 관리 API")
public class ReservationFieldController {

	private static final String HEADER_USER_ID = "X-User-Id";

	private final ReservationFieldService reservationFieldService;

	@GetMapping
	@Operation(summary = "예약 필드 조회", description = "룸의 예약 필드 목록을 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<List<ReservationFieldResponse>> getReservationFields(
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId) {
		List<ReservationFieldResponse> fields = reservationFieldService.getReservationFields(roomId);
		return ResponseEntity.ok(fields);
	}

	@PostMapping
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "예약 필드 추가", description = "룸에 새로운 예약 필드를 추가합니다")
	@ApiResponse(responseCode = "201", description = "추가 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<ReservationFieldResponse> addReservationField(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId,
			@Valid @RequestBody ReservationFieldRequest request) {
		log.info("예약 필드 추가 요청: roomId={}, userId={}", roomId, userId);

		ReservationFieldResponse field = reservationFieldService.addReservationField(roomId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(field);
	}

	@PutMapping
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "예약 필드 전체 교체", description = "룸의 모든 예약 필드를 새 목록으로 교체합니다")
	@ApiResponse(responseCode = "200", description = "교체 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<List<ReservationFieldResponse>> replaceReservationFields(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId,
			@Valid @RequestBody List<ReservationFieldRequest> requests) {
		log.info("예약 필드 전체 교체 요청: roomId={}, userId={}", roomId, userId);

		List<ReservationFieldResponse> fields = reservationFieldService.replaceReservationFields(roomId, requests);
		return ResponseEntity.ok(fields);
	}

	@DeleteMapping("/{fieldId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "예약 필드 삭제", description = "특정 예약 필드를 삭제합니다")
	@ApiResponse(responseCode = "204", description = "삭제 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸 또는 필드")
	public ResponseEntity<Void> deleteReservationField(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId,
			@Parameter(description = "필드 ID", required = true) @PathVariable Long fieldId) {
		log.info("예약 필드 삭제 요청: roomId={}, fieldId={}, userId={}", roomId, fieldId, userId);

		reservationFieldService.deleteReservationField(roomId, fieldId);
		return ResponseEntity.noContent().build();
	}
}
