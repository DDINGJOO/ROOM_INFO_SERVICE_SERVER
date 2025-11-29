package com.teambind.springproject.mapper;

import com.teambind.springproject.dto.response.ImageInfo;
import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.image.RoomImage;
import com.teambind.springproject.entity.attribute.keyword.RoomOptionsMapper;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RoomQueryMapper {
	
	public RoomSimpleResponse toSimpleResponse(RoomInfo roomInfo) {
		// 이미지 정보를 구조화된 형태로 변환
		List<ImageInfo> images = roomInfo.getRoomImages().stream()
				.map(ImageInfo::fromEntity)
				.filter(Objects::nonNull)  // null 값 필터링
				.sorted(Comparator.comparing(ImageInfo::getSequence))  // sequence 순으로 정렬
				.collect(Collectors.toList());

		return RoomSimpleResponse.builder()
				.roomId(roomInfo.getRoomId())
				.roomName(roomInfo.getRoomName())
				.placeId(roomInfo.getPlaceId())
				.timeSlot(roomInfo.getTimeSlot())
				.maxOccupancy(roomInfo.getMaxOccupancy())
				.images(images)  // 구조화된 이미지 정보
				.imageUrls(  // 호환성을 위해 유지
						images.stream()
								.map(ImageInfo::getImageUrl)
								.collect(Collectors.toList())
				)
				.keywordIds(
						roomInfo.getRoomOptions().stream()
								.map(mapper -> mapper.getKeyword().getKeywordId())
								.collect(Collectors.toList())
				)
				.build();
	}
	
	public List<RoomSimpleResponse> toSimpleResponseList(List<RoomInfo> roomInfoList) {
		return roomInfoList.stream()
				.map(this::toSimpleResponse)
				.collect(Collectors.toList());
	}
	
	public RoomDetailResponse toDetailResponse(RoomInfo roomInfo) {
		// 이미지 정보를 구조화된 형태로 변환
		List<ImageInfo> images = roomInfo.getRoomImages().stream()
				.map(ImageInfo::fromEntity)
				.filter(Objects::nonNull)  // null 값 필터링
				.sorted(Comparator.comparing(ImageInfo::getSequence))  // sequence 순으로 정렬
				.collect(Collectors.toList());

		return RoomDetailResponse.builder()
				.roomId(roomInfo.getRoomId())
				.roomName(roomInfo.getRoomName())
				.placeId(roomInfo.getPlaceId())
				.status(roomInfo.getStatus())
				.timeSlot(roomInfo.getTimeSlot())
				.maxOccupancy(roomInfo.getMaxOccupancy())
				.furtherDetails(
						roomInfo.getFurtherDetails().stream()
								.map(FurtherDetail::getContents)
								.collect(Collectors.toList())
				)
				.cautionDetails(
						roomInfo.getCautionDetails().stream()
								.map(CautionDetail::getContents)
								.collect(Collectors.toList())
				)
				.images(images)  // 구조화된 이미지 정보
				.imageUrls(  // 호환성을 위해 유지
						images.stream()
								.map(ImageInfo::getImageUrl)
								.collect(Collectors.toList())
				)
				.keywordIds(
						roomInfo.getRoomOptions().stream()
								.map(RoomOptionsMapper::getKeyword)
								.map(keyword -> keyword.getKeywordId())
								.collect(Collectors.toList())
				)
				.build();
	}
	
	public List<RoomDetailResponse> toDetailResponseList(List<RoomInfo> roomInfoList) {
		return roomInfoList.stream()
				.map(this::toDetailResponse)
				.collect(Collectors.toList());
	}
}
