package com.teambind.springproject.event.events;

import com.teambind.springproject.entity.enums.TimeSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomCreatedEvent extends Event {
	private Long roomId;
	private Long placeId;
	private TimeSlot timeSlot;


	public RoomCreatedEvent(Long roomId, Long placeId, TimeSlot timeSlot) {
		super("room-created");
		this.roomId = roomId;
		this.placeId = placeId;
		this.timeSlot = timeSlot;
	}
}
