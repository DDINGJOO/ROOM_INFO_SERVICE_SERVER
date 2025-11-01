package com.teambind.springproject.event.events;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomDeletedEvent extends Event {
	private Long roomId;
	
	public RoomDeletedEvent(Long roomId) {
		super("room-deleted");
		this.roomId = roomId;
	}
}
