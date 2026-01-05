package com.teambind.springproject.controller.swagger;

import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.dto.request.RoomUpdateRequest;
import com.teambind.springproject.entity.enums.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Room Command", description = "룸 등록/수정/삭제 API (PLACE_MANAGER 전용)")
public interface RoomCommandControllerSwagger {

	@Operation(summary = "룸 생성", description = "새로운 룸을 등록합니다")
	@ApiResponse(responseCode = "201", description = "생성 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	ResponseEntity<Long> createRoom(
			String userId,
			RoomCreateRequest request
	);

	@Operation(summary = "룸 수정", description = "룸 정보를 수정합니다")
	@ApiResponse(responseCode = "200", description = "수정 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<Long> updateRoom(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId,
			RoomUpdateRequest request
	);

	@Operation(summary = "룸 상태 변경", description = "룸의 활성화/비활성화 상태를 변경합니다")
	@ApiResponse(responseCode = "200", description = "상태 변경 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<Long> updateRoomStatus(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId,
			@Parameter(description = "변경할 상태", required = true) Status status
	);

	@Operation(summary = "PENDING 룸 종료", description = "PENDING 상태의 룸을 CLOSE로 변경합니다")
	@ApiResponse(responseCode = "200", description = "종료 성공")
	@ApiResponse(responseCode = "400", description = "PENDING 상태가 아닌 룸")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<Long> closePendingRoom(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId
	);

	@Operation(summary = "룸 삭제", description = "룸을 삭제합니다")
	@ApiResponse(responseCode = "200", description = "삭제 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	ResponseEntity<Long> deleteRoom(
			String userId,
			@Parameter(description = "룸 ID", required = true) Long roomId
	);
}
