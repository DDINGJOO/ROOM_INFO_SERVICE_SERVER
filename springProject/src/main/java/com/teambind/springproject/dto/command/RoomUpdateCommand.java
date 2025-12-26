package com.teambind.springproject.dto.command;

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
public class RoomUpdateCommand {

	private String roomName;
	private TimeSlot timeSlot;
	private Integer maxOccupancy;
	private List<String> furtherDetails;
	private List<String> cautionDetails;
	private List<Long> keywordIds;
}
