package com.teambind.springproject.exception;

public class RoomNotFoundException extends RuntimeException {

	public RoomNotFoundException() {
		super("존재하지 않는 룸입니다.");
	}

	public RoomNotFoundException(Long roomId) {
		super(String.format("존재하지 않는 룸입니다: %d", roomId));
	}
}
