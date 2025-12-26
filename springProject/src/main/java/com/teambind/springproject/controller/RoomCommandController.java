package com.teambind.springproject.controller;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.dto.command.RoomUpdateCommand;
import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.dto.request.RoomUpdateRequest;
import com.teambind.springproject.entity.enums.AppType;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.exception.ForbiddenException;
import com.teambind.springproject.exception.InvalidRequestException;
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
public class RoomCommandController {

	private static final String HEADER_APP_TYPE = "X-App-Type";
	private static final String HEADER_USER_ID = "X-User-Id";

	private final RoomCommandService roomCommandService;

	private AppType parseAppType(String appTypeHeader) {
		if (appTypeHeader == null || appTypeHeader.isBlank()) {
			throw InvalidRequestException.headerMissing(HEADER_APP_TYPE);
		}
		try {
			return AppType.valueOf(appTypeHeader);
		} catch (IllegalArgumentException e) {
			throw InvalidRequestException.invalidFormat(HEADER_APP_TYPE);
		}
	}

	private AppType parseAppTypeOptional(String appTypeHeader) {
		if (appTypeHeader == null || appTypeHeader.isBlank()) {
			return AppType.GENERAL;
		}
		try {
			return AppType.valueOf(appTypeHeader);
		} catch (IllegalArgumentException e) {
			return AppType.GENERAL;
		}
	}

	private void validatePlaceManagerApp(AppType appType) {
		if (appType != AppType.PLACE_MANAGER) {
			throw ForbiddenException.placeManagerOnly();
		}
	}

	@PostMapping
	public ResponseEntity<Long> createRoom(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestHeader(value = HEADER_USER_ID, required = false) String userId,
			@Valid @RequestBody RoomCreateRequest request
	) {
		validatePlaceManagerApp(parseAppType(appTypeHeader));
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
	public ResponseEntity<Long> updateRoom(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestHeader(value = HEADER_USER_ID, required = false) String userId,
			@PathVariable Long roomId,
			@Valid @RequestBody RoomUpdateRequest request
	) {
		validatePlaceManagerApp(parseAppType(appTypeHeader));
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
	public ResponseEntity<Long> updateRoomStatus(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestHeader(value = HEADER_USER_ID, required = false) String userId,
			@PathVariable Long roomId,
			@RequestParam Status status
	) {
		validatePlaceManagerApp(parseAppType(appTypeHeader));
		log.info("룸 상태 변경 요청: roomId={}, status={}, userId={}", roomId, status, userId);

		Long updatedRoomId = roomCommandService.updateRoomStatus(roomId, status);
		return ResponseEntity.ok(updatedRoomId);
	}

	@PatchMapping("/{roomId}/close")
	public ResponseEntity<Long> closePendingRoom(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestHeader(value = HEADER_USER_ID, required = false) String userId,
			@PathVariable Long roomId
	) {
		parseAppTypeOptional(appTypeHeader);
		log.info("PENDING 룸 종료 요청: roomId={}, userId={}", roomId, userId);

		Long closedRoomId = roomCommandService.closePendingRoom(roomId);
		return ResponseEntity.ok(closedRoomId);
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<Long> deleteRoom(
			@RequestHeader(value = HEADER_APP_TYPE, required = false) String appTypeHeader,
			@RequestHeader(value = HEADER_USER_ID, required = false) String userId,
			@PathVariable Long roomId
	) {
		validatePlaceManagerApp(parseAppType(appTypeHeader));
		log.info("룸 삭제 요청: roomId={}, userId={}", roomId, userId);

		Long deletedRoomId = roomCommandService.deleteRoom(roomId);
		return ResponseEntity.ok(deletedRoomId);
	}
}
