package com.teambind.springproject.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSearchQuery {
	
	private String roomName;
	private List<Long> keywordIds;
	private Long placeId;
	private List<Long> roomIds;
	private Integer minOccupancy;
}
