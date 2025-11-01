package com.teambind.springproject.mapper;

import com.teambind.springproject.dto.response.RoomDetailResponse;
import com.teambind.springproject.dto.response.RoomSimpleResponse;
import com.teambind.springproject.entity.RoomInfo;
import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.image.RoomImage;
import com.teambind.springproject.entity.attribute.keyword.RoomOptionsMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomQueryMapper {
	
	public RoomSimpleResponse toSimpleResponse(RoomInfo roomInfo) {
		return RoomSimpleResponse.builder()
				.roomId(roomInfo.getRoomId())
				.roomName(roomInfo.getRoomName())
				.placeId(roomInfo.getPlaceId())
				.timeSlot(roomInfo.getTimeSlot())
				.imageUrls(
						roomInfo.getRoomImages().stream()
								.map(RoomImage::getImageUrl)
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
		return RoomDetailResponse.builder()
				.roomId(roomInfo.getRoomId())
				.roomName(roomInfo.getRoomName())
				.placeId(roomInfo.getPlaceId())
				.status(roomInfo.getStatus())
				.timeSlot(roomInfo.getTimeSlot())
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
				.imageUrls(
						roomInfo.getRoomImages().stream()
								.map(RoomImage::getImageUrl)
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
