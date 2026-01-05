package com.teambind.springproject.controller;

import com.teambind.springproject.controller.annotation.RequireRoomManager;
import com.teambind.springproject.controller.annotation.ValidateRoomOwnership;
import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.dto.command.RoomUpdateCommand;
import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.dto.request.RoomUpdateRequest;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.service.RoomCommandService;
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

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Command", description = "룸 등록/수정/삭제 API (PLACE_MANAGER 전용)")
public class RoomCommandController {

	private static final String HEADER_USER_ID = "X-User-Id";

	private final RoomCommandService roomCommandService;

	@PostMapping
	@RequireRoomManager
	@ValidateRoomOwnership(placeIdSource = "body")
	@Operation(summary = "룸 생성", description = "새로운 룸을 등록합니다")
	@ApiResponse(responseCode = "201", description = "생성 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	public ResponseEntity<Long> createRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Valid @RequestBody RoomCreateRequest request
	) {
		log.info("룸 생성 요청: userId={}", userId);

		RoomCreateCommand command = RoomCreateCommand.builder()
				.roomName(request.getRoomName())
				.placeId(request.getPlaceId())
				.timeSlot(request.getTimeSlot())
				.maxOccupancy(request.getMaxOccupancy())
				.furtherDetails(request.getFurtherDetails())
				.cautionDetails(request.getCautionDetails())
				.keywordIds(request.getKeywordIds())
				.build();

		Long roomId = roomCommandService.createRoom(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
	}

	@PutMapping("/{roomId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "룸 수정", description = "룸 정보를 수정합니다")
	@ApiResponse(responseCode = "200", description = "수정 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<Long> updateRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId,
			@Valid @RequestBody RoomUpdateRequest request
	) {
		log.info("룸 수정 요청: roomId={}, userId={}", roomId, userId);

		RoomUpdateCommand command = RoomUpdateCommand.builder()
				.roomName(request.getRoomName())
				.timeSlot(request.getTimeSlot())
				.maxOccupancy(request.getMaxOccupancy())
				.furtherDetails(request.getFurtherDetails())
				.cautionDetails(request.getCautionDetails())
				.keywordIds(request.getKeywordIds())
				.build();

		Long updatedRoomId = roomCommandService.updateRoom(roomId, command);
		return ResponseEntity.ok(updatedRoomId);
	}

	@PatchMapping("/{roomId}/status")
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "룸 상태 변경", description = "룸의 활성화/비활성화 상태를 변경합니다")
	@ApiResponse(responseCode = "200", description = "상태 변경 성공")
	@ApiResponse(responseCode = "400", description = "잘못된 요청")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<Long> updateRoomStatus(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId,
			@Parameter(description = "변경할 상태", required = true) @RequestParam Status status
	) {
		log.info("룸 상태 변경 요청: roomId={}, status={}, userId={}", roomId, status, userId);

		Long updatedRoomId = roomCommandService.updateRoomStatus(roomId, status);
		return ResponseEntity.ok(updatedRoomId);
	}

	@PatchMapping("/{roomId}/close")
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "PENDING 룸 종료", description = "PENDING 상태의 룸을 CLOSE로 변경합니다")
	@ApiResponse(responseCode = "200", description = "종료 성공")
	@ApiResponse(responseCode = "400", description = "PENDING 상태가 아닌 룸")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<Long> closePendingRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId
	) {
		log.info("PENDING 룸 종료 요청: roomId={}, userId={}", roomId, userId);

		Long closedRoomId = roomCommandService.closePendingRoom(roomId);
		return ResponseEntity.ok(closedRoomId);
	}

	@DeleteMapping("/{roomId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	@Operation(summary = "룸 삭제", description = "룸을 삭제합니다")
	@ApiResponse(responseCode = "200", description = "삭제 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음")
	@ApiResponse(responseCode = "404", description = "존재하지 않는 룸")
	public ResponseEntity<Long> deleteRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@Parameter(description = "룸 ID", required = true) @PathVariable Long roomId
	) {
		log.info("룸 삭제 요청: roomId={}, userId={}", roomId, userId);

		Long deletedRoomId = roomCommandService.deleteRoom(roomId);
		return ResponseEntity.ok(deletedRoomId);
	}
}
