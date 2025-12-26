package com.teambind.springproject.entity;

import com.teambind.springproject.entity.attribute.StringBased.CautionDetail;
import com.teambind.springproject.entity.attribute.StringBased.FurtherDetail;
import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import com.teambind.springproject.entity.attribute.StringBased.StringAttribute;
import com.teambind.springproject.entity.attribute.image.RoomImage;
import com.teambind.springproject.entity.attribute.keyword.RoomOptionsMapper;
import com.teambind.springproject.entity.enums.Status;
import com.teambind.springproject.entity.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "room_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfo {
	
	private static final int MAX_ROOM_IMAGES = 10;
	
	@Id
	@Column(name = "room_id")
	private Long roomId;
	
	@Column(name = "room_name", nullable = false)
	private String roomName;
	
	// 해당 룸을 관리하는 공간 ID
	@Column(name = "place_id", nullable = false)
	private Long placeId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "time_slot")
	private TimeSlot timeSlot;

	@Column(name = "max_occupancy")
	private Integer maxOccupancy;

	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<FurtherDetail> furtherDetails = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<CautionDetail> cautionDetails = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<RoomImage> roomImages = new ArrayList<>();
	
	@OneToMany(mappedBy = "roomInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<RoomOptionsMapper> roomOptions = new ArrayList<>();
	
	
	// 컬렉션 캡슐화 - 불변 컬렉션 반환
	public List<FurtherDetail> getFurtherDetails() {
		return Collections.unmodifiableList(furtherDetails);
	}
	
	public List<CautionDetail> getCautionDetails() {
		return Collections.unmodifiableList(cautionDetails);
	}
	
	public List<RoomImage> getRoomImages() {
		return Collections.unmodifiableList(roomImages);
	}
	
	public List<RoomOptionsMapper> getRoomOptions() {
		return Collections.unmodifiableList(roomOptions);
	}
	
	
	// ===== FurtherDetail 관련 메서드 =====
	public void addFurtherDetail(FurtherDetail detail) {
		validateCanAddDetail(detail, furtherDetails.size());
		detail.assignRoom(this);
		furtherDetails.add(detail);
	}
	
	public void removeFurtherDetail(FurtherDetail detail) {
		furtherDetails.remove(detail);
		detail.removeRoom();
	}
	
	// ===== CautionDetail 관련 메서드 =====
	public void addCautionDetail(CautionDetail detail) {
		validateCanAddDetail(detail, cautionDetails.size());
		detail.assignRoom(this);
		cautionDetails.add(detail);
	}
	
	public void removeCautionDetail(CautionDetail detail) {
		cautionDetails.remove(detail);
		detail.removeRoom();
	}
	
	// ===== RoomImage 관련 메서드 =====

	/**
	 * sequence를 지정하여 이미지를 추가합니다.
	 * @param imageId 이미지 고유 식별자
	 * @param imageUrl 이미지 URL
	 * @param sequence 이미지 순서 (1부터 시작)
	 */
	public void addRoomImageWithSequence(String imageId, String imageUrl, Long sequence) {
		if (imageId == null || imageUrl == null) {
			throw new IllegalArgumentException("Cannot add image with null imageId or imageUrl");
		}

		if (roomImages.size() >= MAX_ROOM_IMAGES) {
			throw new IllegalStateException(
					String.format("방 이미지는 최대 %d개까지 추가할 수 있습니다.", MAX_ROOM_IMAGES)
			);
		}

		if (sequence == null || sequence < 1) {
			// sequence가 유효하지 않으면 자동 sequence 사용
			sequence = (long) (roomImages.size() + 1);
		}

		RoomImage roomImage = new RoomImage(imageId, imageUrl, sequence, this);
		roomImage.assignRoom(this);
		roomImages.add(roomImage);
	}

	/**
	 * 자동 sequence로 이미지를 추가합니다.
	 * @param imageId 이미지 고유 식별자
	 * @param imageUrl 이미지 URL
	 */
	public void addRoomImage(String imageId, String imageUrl) {
		long sequence = roomImages.size() + 1;
		addRoomImageWithSequence(imageId, imageUrl, sequence);
	}

	/**
	 * 기존 메소드 - 호환성 유지를 위해 보존
	 * @deprecated addRoomImage(String, String) 또는 addRoomImageWithSequence 사용 권장
	 */
	@Deprecated
	public void addRoomImage(RoomImage roomImage) {
		if (roomImages.size() >= MAX_ROOM_IMAGES) {
			throw new IllegalStateException(
					String.format("방 이미지는 최대 %d개까지 추가할 수 있습니다.", MAX_ROOM_IMAGES)
			);
		}
		roomImage.assignRoom(this);
		roomImages.add(roomImage);
	}

	public void removeRoomImage(RoomImage roomImage) {
		roomImages.remove(roomImage);
		roomImage.removeRoom();
	}
	
	// ===== Keyword 관련 메서드 =====
	public void addKeyword(Keyword keyword) {
		if (hasKeyword(keyword)) {
			throw new IllegalStateException("이미 등록된 키워드입니다.");
		}
		
		RoomOptionsMapper mapper = RoomOptionsMapper.create(this, keyword);
		mapper.assignRoom(this);
		roomOptions.add(mapper);
	}
	
	public void removeKeyword(Keyword keyword) {
		roomOptions.removeIf(mapper -> {
			boolean matches = mapper.getKeyword().getKeywordId().equals(keyword.getKeywordId());
			if (matches) {
				mapper.removeRoom();
			}
			return matches;
		});
	}
	
	// ===== 비즈니스 로직 (검증) =====
	private void validateCanAddDetail(StringAttribute detail, int currentSize) {
		int maxLimit = detail.getMaxLimit();
		if (currentSize >= maxLimit) {
			String detailType = detail instanceof FurtherDetail ? "추가 정보" : "주의 사항";
			throw new IllegalStateException(
					String.format("%s는 최대 %d개까지 추가할 수 있습니다.", detailType, maxLimit)
			);
		}
	}

	private boolean hasKeyword(Keyword keyword) {
		return roomOptions.stream()
				.anyMatch(mapper -> mapper.getKeyword().getKeywordId().equals(keyword.getKeywordId()));
	}

	public void validateMaxOccupancy() {
		if (maxOccupancy != null && maxOccupancy < 1) {
			throw new IllegalArgumentException("최대 수용 인원은 1명 이상이어야 합니다.");
		}
	}

	public void updateMaxOccupancy(Integer maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
		validateMaxOccupancy();
	}

	public void updateRoomName(String roomName) {
		if (roomName != null && !roomName.isBlank()) {
			this.roomName = roomName;
		}
	}

	public void updateTimeSlot(TimeSlot timeSlot) {
		if (timeSlot != null) {
			this.timeSlot = timeSlot;
		}
	}

	public void updateStatus(Status status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void closePendingRoom() {
		if (this.status != Status.PENDING) {
			throw new IllegalStateException("PENDING 상태의 룸만 CLOSE로 변경할 수 있습니다.");
		}
		this.status = Status.CLOSE;
	}

	public void clearFurtherDetails() {
		this.furtherDetails.forEach(detail -> detail.removeRoom());
		this.furtherDetails.clear();
	}

	public void clearCautionDetails() {
		this.cautionDetails.forEach(detail -> detail.removeRoom());
		this.cautionDetails.clear();
	}

	public void clearKeywords() {
		this.roomOptions.forEach(mapper -> mapper.removeRoom());
		this.roomOptions.clear();
	}

	public int getRoomImageCount() {
		return roomImages.size();
	}
	
	public int getFurtherDetailCount() {
		return furtherDetails.size();
	}
	
	public int getCautionDetailCount() {
		return cautionDetails.size();
	}
	
	public int getKeywordCount() {
		return roomOptions.size();
	}
}
