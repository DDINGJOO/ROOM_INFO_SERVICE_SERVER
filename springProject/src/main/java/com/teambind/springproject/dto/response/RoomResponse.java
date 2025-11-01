package com.teambind.springproject.dto.response;

import com.teambind.springproject.entity.enums.Status;
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
public class RoomResponse {
	
	private Long roomId;
	private String roomName;
	private Long placeId;
	private Status status;
	private TimeSlot timeSlot;
	private List<String> furtherDetails;
	private List<String> cautionDetails;
	private List<Long> keywordIds;
}
