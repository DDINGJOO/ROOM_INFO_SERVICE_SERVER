package com.teambind.springproject.dto.projection;

import com.teambind.springproject.entity.enums.TimeSlot;

import java.util.List;

public interface RoomSimpleProjection {
	Long getRoomId();
	
	String getRoomName();
	
	Long getPlaceId();
	
	TimeSlot getTimeSlot();
	
	List<String> getImageUrls();
	
	List<Long> getKeywordIds();
}
