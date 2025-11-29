package com.teambind.springproject.dto.request;

import jakarta.validation.constraints.Min;
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

	@Min(value = 1, message = "최소 수용 인원은 1명 이상이어야 합니다.")
	private Integer minOccupancy;
}
