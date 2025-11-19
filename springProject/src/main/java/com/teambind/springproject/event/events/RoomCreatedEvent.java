package com.teambind.springproject.event.events;

import com.teambind.springproject.entity.enums.TimeSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomCreatedEvent extends Event {
	private String roomId;
	private String placeId;
	private TimeSlot timeSlot;
	
	/**
	 * Long ID를 받아 내부적으로 String으로 변환하여 저장
	 * JavaScript Number 정밀도 손실 방지 (MAX_SAFE_INTEGER: 2^53-1)
	 */
	public RoomCreatedEvent(Long roomId, Long placeId, TimeSlot timeSlot) {
		super("room-created");
		this.roomId = String.valueOf(roomId);
		this.placeId = String.valueOf(placeId);
		this.timeSlot = timeSlot;
	}
}
