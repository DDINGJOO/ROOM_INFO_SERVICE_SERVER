package com.teambind.springproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSearchRequest {

	private String roomName;
	private List<Long> keywordIds;
	private Long placeId;
}
