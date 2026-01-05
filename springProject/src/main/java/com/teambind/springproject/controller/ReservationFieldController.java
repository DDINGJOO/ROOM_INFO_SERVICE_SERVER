package com.teambind.springproject.controller;

import com.teambind.springproject.controller.annotation.RequireRoomManager;
import com.teambind.springproject.controller.annotation.ValidateRoomOwnership;
import com.teambind.springproject.controller.swagger.ReservationFieldControllerSwagger;
import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import com.teambind.springproject.service.ReservationFieldService;
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
public class ReservationFieldController implements ReservationFieldControllerSwagger {

	private static final String HEADER_USER_ID = "X-User-Id";

	private final ReservationFieldService reservationFieldService;

	@Override
	@GetMapping
	public ResponseEntity<List<ReservationFieldResponse>> getReservationFields(
			@PathVariable Long roomId
	) {
		List<ReservationFieldResponse> fields = reservationFieldService.getReservationFields(roomId);
		return ResponseEntity.ok(fields);
	}

	@Override
	@PostMapping
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<ReservationFieldResponse> addReservationField(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId,
			@Valid @RequestBody ReservationFieldRequest request
	) {
		log.info("예약 필드 추가 요청: roomId={}, userId={}", roomId, userId);

		ReservationFieldResponse field = reservationFieldService.addReservationField(roomId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(field);
	}

	@Override
	@PutMapping
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<List<ReservationFieldResponse>> replaceReservationFields(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId,
			@Valid @RequestBody List<ReservationFieldRequest> requests
	) {
		log.info("예약 필드 전체 교체 요청: roomId={}, userId={}", roomId, userId);

		List<ReservationFieldResponse> fields = reservationFieldService.replaceReservationFields(roomId, requests);
		return ResponseEntity.ok(fields);
	}

	@Override
	@DeleteMapping("/{fieldId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<Void> deleteReservationField(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId,
			@PathVariable Long fieldId
	) {
		log.info("예약 필드 삭제 요청: roomId={}, fieldId={}, userId={}", roomId, fieldId, userId);

		reservationFieldService.deleteReservationField(roomId, fieldId);
		return ResponseEntity.noContent().build();
	}
}
