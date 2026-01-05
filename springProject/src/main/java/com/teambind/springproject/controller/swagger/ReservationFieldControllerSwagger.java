package com.teambind.springproject.controller.swagger;

import com.teambind.springproject.dto.request.ReservationFieldRequest;
import com.teambind.springproject.dto.response.ReservationFieldResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Reservation Field", description = "예약 필드 관리 API")
public interface ReservationFieldControllerSwagger {

	@Operation(summary = "예약 필드 조회", description = "룸의 예약 필드 목록을 조회합니다")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<List<ReservationFieldResponse>> getReservationFields(
			@Parameter(description = "룸 ID", required = true) Long roomId
	);

	@Operation(summary = "예약 필드 추가", description = "룸에 새로운 예약 필드를 추가합니다")
	@ApiResponse(responseCode = "201", description = "추가 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<ReservationFieldResponse> addReservationField(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId,
			ReservationFieldRequest request
	);

	@Operation(summary = "예약 필드 전체 교체", description = "룸의 모든 예약 필드를 새 목록으로 교체합니다")
	@ApiResponse(responseCode = "200", description = "교체 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<List<ReservationFieldResponse>> replaceReservationFields(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId,
			List<ReservationFieldRequest> requests
	);

	@Operation(summary = "예약 필드 삭제", description = "특정 예약 필드를 삭제합니다")
	@ApiResponse(responseCode = "204", description = "삭제 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸 또는 필드")
	ResponseEntity<Void> deleteReservationField(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId,
			@Parameter(description = "필드 ID", required = true) Long fieldId
	);
}
