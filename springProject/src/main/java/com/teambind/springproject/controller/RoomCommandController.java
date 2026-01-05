package com.teambind.springproject.controller;

import com.teambind.springproject.controller.annotation.RequireRoomManager;
import com.teambind.springproject.controller.annotation.ValidateRoomOwnership;
import com.teambind.springproject.controller.swagger.RoomCommandControllerSwagger;
import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.dto.command.RoomUpdateCommand;
import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.dto.request.RoomUpdateRequest;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.service.RoomCommandService;
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
public class RoomCommandController implements RoomCommandControllerSwagger {

	private static final String HEADER_USER_ID = "X-User-Id";

	private final RoomCommandService roomCommandService;

	@Override
	@PostMapping
	@RequireRoomManager
	@ValidateRoomOwnership(placeIdSource = "body")
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

	@Override
	@PutMapping("/{roomId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<Long> updateRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId,
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

	@Override
	@PatchMapping("/{roomId}/status")
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<Long> updateRoomStatus(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId,
			@RequestParam Status status
	) {
		log.info("룸 상태 변경 요청: roomId={}, status={}, userId={}", roomId, status, userId);

		Long updatedRoomId = roomCommandService.updateRoomStatus(roomId, status);
		return ResponseEntity.ok(updatedRoomId);
	}

	@Override
	@PatchMapping("/{roomId}/close")
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<Long> closePendingRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId
	) {
		log.info("PENDING 룸 종료 요청: roomId={}, userId={}", roomId, userId);

		Long closedRoomId = roomCommandService.closePendingRoom(roomId);
		return ResponseEntity.ok(closedRoomId);
	}

	@Override
	@DeleteMapping("/{roomId}")
	@RequireRoomManager
	@ValidateRoomOwnership
	public ResponseEntity<Long> deleteRoom(
			@RequestHeader(value = HEADER_USER_ID) String userId,
			@PathVariable Long roomId
	) {
		log.info("룸 삭제 요청: roomId={}, userId={}", roomId, userId);

		Long deletedRoomId = roomCommandService.deleteRoom(roomId);
		return ResponseEntity.ok(deletedRoomId);
	}
}
