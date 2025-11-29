package com.teambind.springproject.dto.request;

import com.teambind.springproject.entity.enums.TimeSlot;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateRequest {
	
	@NotBlank(message = "방 이름은 필수입니다.")
	private String roomName;
	
	@NotNull(message = "Place ID는 필수입니다.")
	private Long placeId;
	
	private TimeSlot timeSlot;

	@Min(value = 1, message = "최대 수용 인원은 1명 이상이어야 합니다.")
	private Integer maxOccupancy;

	@Size(max = 7, message = "추가 정보는 최대 7개까지 가능합니다.")
	private List<String> furtherDetails;
	
	@Size(max = 8, message = "주의 사항은 최대 8개까지 가능합니다.")
	private List<String> cautionDetails;
	
	private List<Long> keywordIds;
}
