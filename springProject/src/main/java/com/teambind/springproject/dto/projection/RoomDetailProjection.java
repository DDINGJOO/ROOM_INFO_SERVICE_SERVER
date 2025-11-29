package com.teambind.springproject.dto.projection;

import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;

import java.util.List;

public interface RoomDetailProjection {
	Long getRoomId();
	
	String getRoomName();
	
	Long getPlaceId();
	
	Status getStatus();

	TimeSlot getTimeSlot();

	Integer getMaxOccupancy();

	List<String> getFurtherDetails();
	
	List<String> getCautionDetails();
	
	List<String> getImageUrls();
	
	List<Long> getKeywordIds();
}
