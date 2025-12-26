package com.teambind.springproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
				Map.of(
						"timestamp", LocalDateTime.now().toString(),
						"status", 403,
						"error", "Forbidden",
						"message", e.getMessage()
				)
		);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidRequestException(InvalidRequestException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				Map.of(
						"timestamp", LocalDateTime.now().toString(),
						"status", 400,
						"error", "Bad Request",
						"message", e.getMessage()
				)
		);
	}

	@ExceptionHandler(RoomNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleRoomNotFoundException(RoomNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				Map.of(
						"timestamp", LocalDateTime.now().toString(),
						"status", 404,
						"error", "Not Found",
						"message", e.getMessage()
				)
		);
	}
}
