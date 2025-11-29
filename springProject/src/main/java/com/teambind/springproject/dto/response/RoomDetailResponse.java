package com.teambind.springproject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class RoomDetailResponse {

	private Long roomId;
	private String roomName;
	private Long placeId;
	private Status status;
	private TimeSlot timeSlot;
	private Integer maxOccupancy;
	private List<String> furtherDetails;
	private List<String> cautionDetails;

	/**
	 * 구조화된 이미지 정보 목록
	 * @deprecated imageUrls 대신 images 사용 권장
	 */
	@Deprecated
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> imageUrls;

	/** 구조화된 이미지 정보 목록 (imageId, imageUrl, sequence 포함) */
	private List<ImageInfo> images;

	private List<Long> keywordIds;
}
