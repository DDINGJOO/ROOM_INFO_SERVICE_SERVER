package com.teambind.springproject.dto.response;

import com.teambind.springproject.entity.enums.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSimpleResponse {
	
	private Long roomId;
	private String roomName;
	private Long placeId;
	private TimeSlot timeSlot;
	private Integer maxOccupancy;
	private List<String> imageUrls;
	private List<Long> keywordIds;
}
