package com.teambind.springproject.controller;

import com.teambind.springproject.dto.command.RoomCreateCommand;
import com.teambind.springproject.dto.request.RoomCreateRequest;
import com.teambind.springproject.service.RoomCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomCommandController {
	
	private final RoomCommandService roomCommandService;
	
	@PostMapping
	public ResponseEntity<Long> createRoom(@Valid @RequestBody RoomCreateRequest request) {
		RoomCreateCommand command = RoomCreateCommand.builder()
				.roomName(request.getRoomName())
				.placeId(request.getPlaceId())
				.timeSlot(request.getTimeSlot())
				.furtherDetails(request.getFurtherDetails())
				.cautionDetails(request.getCautionDetails())
				.keywordIds(request.getKeywordIds())
				.build();
		
		Long roomId = roomCommandService.createRoom(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
	}
	
	@DeleteMapping("/{roomId}")
	public ResponseEntity<Long> deleteRoom(@PathVariable Long roomId) {
		Long deletedRoomId = roomCommandService.deleteRoom(roomId);
		return ResponseEntity.ok(deletedRoomId);
	}
}
