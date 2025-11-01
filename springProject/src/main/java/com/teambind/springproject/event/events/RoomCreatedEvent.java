package com.teambind.springproject.event.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomCreatedEvent extends Event {
	private Long roomId;
	
	public RoomCreatedEvent(Long roomId) {
		super("room-created");
		this.roomId = roomId;
	}
}
