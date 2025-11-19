package com.teambind.springproject.event.events;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomDeletedEvent extends Event {
	private String roomId;
	
	/**
	 * Long ID를 받아 내부적으로 String으로 변환하여 저장
	 * JavaScript Number 정밀도 손실 방지 (MAX_SAFE_INTEGER: 2^53-1)
	 */
	public RoomDeletedEvent(Long roomId) {
		super("room-deleted");
		this.roomId = String.valueOf(roomId);
	}
}
